package com.example.springreactive.chapter18.book;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

  Mono<Book> findByIsbn(String isbn);
}
