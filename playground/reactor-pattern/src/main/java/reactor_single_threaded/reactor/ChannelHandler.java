package reactor_single_threaded.reactor;

public interface ChannelHandler {

  void read() throws Exception;

  void write() throws Exception;
}
