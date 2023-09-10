package com.example.springreactive.chapter19.book;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookHandler {

  private final BookMapper mapper;
  private final BookValidator validator;
  private final BookService bookService;

  public Mono<ServerResponse> createBook(ServerRequest request) {
    return request.bodyToMono(BookDto.Post.class)
        .doOnNext(post -> validator.validate(post))
        .flatMap(post -> bookService.saveBook(mapper.bookPostToBook(post)))
        .flatMap(book -> ServerResponse
            .created(URI.create("/v5/books/" + book.getBookId()))
            .build());
  }

  public Mono<ServerResponse> updateBook(ServerRequest request) {
    final long bookId = Long.parseLong(request.pathVariable("book-id"));
    return request
        .bodyToMono(BookDto.Patch.class)
        .doOnNext(patch -> validator.validate(patch))
        .flatMap(patch -> {
          patch.setBookId(bookId);
          return bookService.updateBook(mapper.bookPatchToBook(patch));
        })
        .flatMap(book -> ServerResponse.ok()
            .bodyValue(mapper.bookToResponse(book)));
  }

  public Mono<ServerResponse> getBook(ServerRequest request) {
    long bookId = Long.parseLong(request.pathVariable("book-id"));

    return bookService.findBook(bookId)
        .flatMap(book -> ServerResponse
            .ok()
            .bodyValue(mapper.bookToResponse(book)));
  }

  public Mono<ServerResponse> getBooks(ServerRequest request) {
    Tuple2<Integer, Integer> pageAndSize = getPageAndSize(request);
    return bookService.findBooks(pageAndSize.getT1(), pageAndSize.getT2())
        .flatMap(books -> ServerResponse
            .ok()
            .bodyValue(mapper.booksToResponse(books)));
  }

  private Tuple2<Integer, Integer> getPageAndSize(ServerRequest request) {
    int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
    int size = request.queryParam("size").map(Integer::parseInt).orElse(0);
    return Tuples.of(page, size);
  }
}
