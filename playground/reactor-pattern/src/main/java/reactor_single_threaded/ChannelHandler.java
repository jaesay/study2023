package reactor_single_threaded;

public interface ChannelHandler {

  void read() throws Exception;

  void write() throws Exception;
}
