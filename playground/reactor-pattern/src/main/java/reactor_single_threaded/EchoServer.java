package reactor_single_threaded;

import reactor_single_threaded.reactor.Reactor;

public class EchoServer {

  public static void main(String[] args) throws Exception {
    new Reactor().bind(8080);
  }
}
