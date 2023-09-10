package com.example.springreactive.chapter18.book;

import com.example.springreactive.chapter18.book.exception.BusinessLogicException;
import com.example.springreactive.chapter18.book.exception.ExceptionCode;
import com.example.springreactive.chapter18.book.utils.CustomBeanUtils;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

  private final @NonNull BookRepository bookRepository;
  private final @NonNull CustomBeanUtils<Book> beanUtils;

  public Mono<Book> saveBook(Book book) {
    return verifyExistIsbn(book.getIsbn())
        .then(bookRepository.save(book));
  }

  public Mono<Book> updateBook(Book book) {
    return findVerifiedBook(book.getBookId())
        .map(findBook -> beanUtils.copyNonNullProperties(book, findBook))
        .flatMap(updatingBook -> bookRepository.save(updatingBook));
  }

  public Mono<Book> findBook(long bookId) {
    return findVerifiedBook(bookId);
  }

  public Mono<List<Book>> findBooks() {
    return bookRepository.findAll().collectList();
  }

  public Mono<List<Book>> findBooks(@Positive int page, @Positive int size) {
    return bookRepository
        .findAllBy(PageRequest.of(page - 1, size,
            Sort.by("bookId").descending()))
        .collectList();
  }

  private Mono<Void> verifyExistIsbn(String isbn) {
    return bookRepository.findByIsbn(isbn)
        .flatMap(findBook -> {
          if (findBook != null) {
            return Mono.error(new BusinessLogicException(ExceptionCode.BOOK_EXISTS));
          }
          return Mono.empty();
        });
  }

  private Mono<Book> findVerifiedBook(long bookId) {
    return bookRepository
        .findById(bookId)
        .switchIfEmpty(Mono.error(new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND)));
  }
}
