package com.example.springdatareactiveredis;

import java.util.Collection;
import java.util.List;
import reactor.core.publisher.Mono;

public interface CustomReactiveCrudRepository<T, ID> {

  Mono<T> findById(ID id);
  Mono<List<T>> findAllById(Collection<ID> ids);
}
