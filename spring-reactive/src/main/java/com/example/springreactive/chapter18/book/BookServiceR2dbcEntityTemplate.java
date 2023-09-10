package com.example.springreactive.chapter18.book;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.example.springreactive.chapter18.book.exception.BusinessLogicException;
import com.example.springreactive.chapter18.book.exception.ExceptionCode;
import com.example.springreactive.chapter18.book.utils.CustomBeanUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceR2dbcEntityTemplate {

  private final @NonNull R2dbcEntityTemplate template;
  private final @NonNull CustomBeanUtils<Book> beanUtils;

  public Mono<Book> saveBook(Book book) {
    return verifyExistIsbn(book.getIsbn())
        .then(template.insert(book));
  }

  public Mono<Book> updateBook(Book book) {
    return findVerifiedBook(book.getBookId())
        .map(findBook -> beanUtils.copyNonNullProperties(book, findBook))
        .flatMap(updatingBook -> template.update(updatingBook));
  }

  public Mono<Book> findBook(long bookId) {
    return findVerifiedBook(bookId);
  }

  public Mono<List<Book>> findBooks() {
    return template.select(Book.class).all().collectList();
  }

  private Mono<Void> verifyExistIsbn(String isbn) {
    return template.selectOne(query(where("ISBN").is(isbn)), Book.class)
        .flatMap(findBook -> {
          if (findBook != null) {
            return Mono.error(new BusinessLogicException(
                ExceptionCode.BOOK_EXISTS));
          }
          return Mono.empty();
        });
  }

  private Mono<Book> findVerifiedBook(long bookId) {
    return template.selectOne(query(where("BOOK_ID").is(bookId))
            , Book.class)
        .switchIfEmpty(Mono.error(new BusinessLogicException(
            ExceptionCode.BOOK_NOT_FOUND)));
  }
}
