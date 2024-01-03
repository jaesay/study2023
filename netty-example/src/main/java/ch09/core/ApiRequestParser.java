package ch09.core;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import ch09.service.ServiceDispatcher;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiRequestParser extends SimpleChannelInboundHandler<FullHttpMessage> {

  private HttpRequest request;

  private JsonObject apiResult;

  private static final HttpDataFactory factory = new DefaultHttpDataFactory(
      DefaultHttpDataFactory.MINSIZE);

  private HttpPostRequestDecoder decoder;

  private Map<String, String> reqData = new HashMap<>();

  private static final Set<String> usingHeader = new HashSet<>();

  static {
    usingHeader.add("token");
    usingHeader.add("email");
  }

  /**
   * channelRead0 이벤트 메서드의 수행이 완료된 이후에 channelReadComplete 메서드가 호출되고 이때 채널 버퍼의 내용을 클라이언트로 전송한다.
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    log.info("요청 처리 완료");
    ctx.flush();
  }

  /**
   * 클라이언트가 전송한 데이터가 채널 파이프라인의 모든 디코더를 거치고 난 뒤에 호출된다. 메서드 호출에 입력되는 객체는 FullHttpMessage 인터페이스의 구현체이고
   * HTTP 프로토콜의 모든 데이터가 포함되어 있다.
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) {
    // Request header 처리.
    if (msg instanceof HttpRequest) {
      this.request = (HttpRequest) msg;

      if (HttpHeaders.is100ContinueExpected(request)) {
        send100Continue(ctx);
      }

      HttpHeaders headers = request.headers();
      if (!headers.isEmpty()) {
        for (Map.Entry<String, String> h : headers) {
          String key = h.getKey();
          if (usingHeader.contains(key)) {
            reqData.put(key, h.getValue());
          }
        }
      }

      reqData.put("REQUEST_URI", request.getUri());
      reqData.put("REQUEST_METHOD", request.getMethod().name());
    }

    // Request content 처리.
    if (msg instanceof HttpContent) {
      // 12. HttpContent의 상위 인터페이스인 LastHttpContent는 모든 HTTP 메시지가 디코딩되었고 HTTP 프로토콜의 마지막 데이터임을 알리는 인터페이스다.
      if (msg instanceof LastHttpContent) {
        log.debug("LastHttpContent message received!!" + request.getUri());

        LastHttpContent trailer = (LastHttpContent) msg;

        readPostData();

        ApiRequest service = ServiceDispatcher.dispatch(reqData);

        try {
          service.executeService();

          apiResult = service.getApiResult();
        } finally {
          reqData.clear();
        }

        // apiResult 맴버 변수에 저장된 API 처리 결과를 클라이언트 채널의 송신 버퍼에 기록한다.
        if (!writeResponse(trailer, ctx)) {
          // If keep-alive is off, close the connection once the content is fully written.
          ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        reset();
      }
    }
  }

  private void reset() {
    request = null;
  }

  /**
   * HTTP 본문 수신한다.
   */
  private void readPostData() {
    try {
      decoder = new HttpPostRequestDecoder(factory, request);
      for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
        if (HttpDataType.Attribute == data.getHttpDataType()) {
          try {
            Attribute attribute = (Attribute) data;
            reqData.put(attribute.getName(), attribute.getValue());
          } catch (IOException e) {
            log.error("BODY Attribute: " + data.getHttpDataType().name(), e);
            return;
          }
        } else {
          log.info("BODY data : " + data.getHttpDataType().name() + ": " + data);
        }
      }
    } catch (ErrorDataDecoderException e) {
      log.error(String.valueOf(e));
    } finally {
      if (decoder != null) {
        decoder.destroy();
      }
    }
  }

  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
    // Decide whether to close the connection or not.
    boolean keepAlive = HttpHeaders.isKeepAlive(request);
    // Build the response object.
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
        currentObj.getDecoderResult().isSuccess() ? OK : BAD_REQUEST, Unpooled.copiedBuffer(
        apiResult.toString(), CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");

    if (keepAlive) {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      // Add keep alive header as per:
      // -
      // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    // Write the response.
    ctx.write(response);

    return keepAlive;
  }

  private static void send100Continue(ChannelHandlerContext ctx) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
    ctx.write(response);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(String.valueOf(cause));
    ctx.close();
  }
}
