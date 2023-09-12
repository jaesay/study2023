package com.example.springreactive.chapter21.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

  Mono<Book> findByIsbn(String isbn);
  Flux<Book> findAllBy(Pageable pageable);
}
