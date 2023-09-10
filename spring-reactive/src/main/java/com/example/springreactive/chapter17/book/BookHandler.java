package com.example.springreactive.chapter17.book;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookHandler {

  private final BookMapper mapper;
//  private final BookValidatorV2 validatorV2;
//  private final BookValidatorV3 validator;
  private final BookValidatorV4 validator;

  public Mono<ServerResponse> createBook(ServerRequest request) {
    return request
        .bodyToMono(BookDto.Post.class)
//        .doOnNext(post -> validate(post))
        .doOnNext(post -> validator.validate(post))
        .map(post -> mapper.bookPostToBook(post))
        .flatMap(book ->
            ServerResponse
                .created(URI.create("/v1/books/" + book.getBookId()))
                .build());
  }

  public Mono<ServerResponse> getBook(ServerRequest request) {
    long bookId = Long.parseLong(request.pathVariable("book-id"));
    Book book = new Book(bookId,
        "Java 고급",
        "Advanced Java",
        "Kevin",
        "111-11-1111-111-1",
        "Java 중급 프로그래밍 마스터",
        "2022-03-22",
        LocalDateTime.now(),
        LocalDateTime.now());

    return ServerResponse
        .ok()
        .bodyValue(mapper.bookToResponse(book))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> updateBook(ServerRequest request) {
    final long bookId = Long.parseLong(request.pathVariable("book-id"));

    return request
        .bodyToMono(BookDto.Patch.class)
        .doOnNext(post -> validator.validate(post))
        .map(patch -> {
          patch.setBookId(bookId);
          return mapper.bookPatchToBook(patch);
        })
        .flatMap(book -> ServerResponse.ok()
            .bodyValue(mapper.bookToResponse(book)));
  }

  public Mono<ServerResponse> getBooks(ServerRequest request) {
    List<Book> books = List.of(
        new Book(1L,
            "Java 고급",
            "Advanced Java",
            "Kevin",
            "111-11-1111-111-1",
            "Java 중급 프로그래밍 마스터",
            "2022-03-22",
            LocalDateTime.now(),
            LocalDateTime.now()),
        new Book(2L,
            "Kotlin 고급",
            "Advanced Kotlin",
            "Kevin",
            "222-22-2222-222-2",
            "Kotlin 중급 프로그래밍 마스터",
            "2022-05-22",
            LocalDateTime.now(),
            LocalDateTime.now())
    );

    return ServerResponse
        .ok()
        .bodyValue(mapper.booksToResponse(books));
  }

//  private void validate(BookDto.Post post) {
//    Errors errors = new BeanPropertyBindingResult(post, BookDto.class.getName());
//    validatorV2.validate(post, errors);
//    if (errors.hasErrors()) {
//      log.error(errors.getAllErrors().toString());
//      throw new ServerWebInputException(errors.toString());
//    }
//  }
}
