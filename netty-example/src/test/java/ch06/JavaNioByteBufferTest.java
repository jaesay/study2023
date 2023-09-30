package ch06;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@SuppressWarnings({"NonAsciiCharacters"})
@DisplayName("자바 NIO 바이트 버퍼 테스트")
class JavaNioByteBufferTest {

  @Test
  void 생성_테스트() {
    CharBuffer heapBuffer = CharBuffer.allocate(11);
    assertThat(heapBuffer.capacity()).isEqualTo(11);
    assertThat(heapBuffer.isDirect()).isFalse();

    ByteBuffer directBuffer = ByteBuffer.allocateDirect(11);
    assertThat(directBuffer.capacity()).isEqualTo(11);
    assertThat(directBuffer.isDirect()).isTrue();

    int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0};
    IntBuffer intHeapBuffer = IntBuffer.wrap(array);
    assertThat(intHeapBuffer.capacity()).isEqualTo(11);
    assertThat(intHeapBuffer.isDirect()).isFalse();
  }

  @Test
  void 데이터_저장_테스트() {
    ByteBuffer firstBuffer = ByteBuffer.allocate(11);
    System.out.println("바이트 버퍼 초깃값 : " + firstBuffer);

    byte[] source = "Hello world".getBytes();
    firstBuffer.put(source);
    System.out.println("11바이트 기록 후 : " + firstBuffer);
  }

  @Test
  void 바이트_버퍼_오버플로우_테스트() {
    ByteBuffer firstBuffer = ByteBuffer.allocate(11);
    System.out.println("초기 상태 : " + firstBuffer);

    byte[] source = "Hello world!".getBytes();
    for (byte b : source) {
      firstBuffer.put(b);
      System.out.println("현재 상태 : " + firstBuffer);
    }
  }

  @Test
  void get_테스트() {
    ByteBuffer firstBuffer = ByteBuffer.allocate(11);
    System.out.println("초기 상태 : " + firstBuffer);

    firstBuffer.put((byte) 1); // 쓰기 후 pos 1 증가
    System.out.println(firstBuffer.get());
    System.out.println("현재 상태 : " + firstBuffer); // 읽기 후 pos 1 증가
  }

  @Test
  void rewind_테스트() {
    ByteBuffer firstBuffer = ByteBuffer.allocate(11);
    System.out.println("초기 상태 : " + firstBuffer);

    firstBuffer.put((byte) 1);
    firstBuffer.put((byte) 2);
    assertThat(firstBuffer.position()).isEqualTo(2);

    firstBuffer.rewind();
    assertThat(firstBuffer.position()).isEqualTo(0);

    assertThat(firstBuffer.get()).isEqualTo((byte) 1);
    assertThat(firstBuffer.position()).isEqualTo(1);

    System.out.println(firstBuffer);
  }

  @Test
  void flip_테스트() {
    ByteBuffer firstBuffer = ByteBuffer.allocate(11);
    assertThat(firstBuffer.position()).isEqualTo(0);
    assertThat(firstBuffer.limit()).isEqualTo(11);

    firstBuffer.put((byte) 1);
    firstBuffer.put((byte) 2);
    firstBuffer.put((byte) 3);
    firstBuffer.put((byte) 4);
    assertThat(firstBuffer.position()).isEqualTo(4);
    assertThat(firstBuffer.limit()).isEqualTo(11);

    firstBuffer.flip();
    assertThat(firstBuffer.position()).isEqualTo(0);
    assertThat(firstBuffer.limit()).isEqualTo(4);
  }

  @Test
  void flip_테스트2() {
    byte[] array = {1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0};
    ByteBuffer bb = ByteBuffer.wrap(array);
    assertThat(bb.position()).isEqualTo(0);
    assertThat(bb.limit()).isEqualTo(11);

    assertThat(bb.get()).isEqualTo((byte) 1);
    assertThat(bb.get()).isEqualTo((byte) 2);
    assertThat(bb.get()).isEqualTo((byte) 3);
    assertThat(bb.get()).isEqualTo((byte) 4);
    assertThat(bb.position()).isEqualTo(4);
    assertThat(bb.limit()).isEqualTo(11);

    // 읽기 작업 또는 쓰기 작업의 완료를 의미
    // 자바의 바이트 버퍼를 사용할 때는 읽기와 쓰기를 분리하여 생각해야 하며 특히 다중 스레드 환경에서 바이트 버퍼를 공유하지 않아야 한다.
    // 네티는 이와 같은 자바 바이트 버퍼의 문제점을 해결하기 위해서 읽기를 위한 인덱스와 쓰기를 위한 인덱스를 별도로 제공한다.
    bb.flip();

    assertThat(bb.position()).isEqualTo(0);
    assertThat(bb.limit()).isEqualTo(4);

    System.out.println(bb.get(3));

    assertThat(bb.position()).isEqualTo(0);
  }




















}
