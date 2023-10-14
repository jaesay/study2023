package com.springreactive.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

class SinksTest {

  @Test
  void sink_replay() {
    Sinks.Many<Object> replay = Sinks.many().replay().all();

    replay.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    replay.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

    Flux<Object> replaySinkFlux = replay.asFlux();
    replaySinkFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));

    Flux<Object> replaySinkFlux2 = replay.asFlux();
    replaySinkFlux2.subscribe(i -> System.out.println("Subscriber 2 : " + i));

    replay.tryEmitNext(3);
  }

  @Test
  void sink_multicast() {
    Sinks.Many<Object> multicast = Sinks.many().multicast().onBackpressureBuffer();

    multicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    multicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

    Flux<Object> multicastFlux = multicast.asFlux();
    multicastFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));

    Flux<Object> multicastFlux2 = multicast.asFlux();
    multicastFlux2.subscribe(i -> System.out.println("Subscriber 2 : " + i));

    multicast.tryEmitNext(3);
  }

  @Test
  void sink_unicast() {
    Sinks.Many<Object> unicast = Sinks.many().unicast().onBackpressureBuffer();

    unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
    unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

    Flux<Object> unicastFlux = unicast.asFlux();
    unicastFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));

    Flux<Object> unicastFlux2 = unicast.asFlux();
    unicastFlux2.subscribe(i -> System.out.println("Subscriber 2 : " + i));

    unicast.tryEmitNext(3);
  }
}
