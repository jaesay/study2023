package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewHandler {

  private final Validator validator;
  private final ReviewReactiveRepository reviewReactiveRepository;

  public Mono<ServerResponse> addReview(ServerRequest request) {
    return request.bodyToMono(Review.class)
        .doOnNext(this::validate)
        .flatMap(reviewReactiveRepository::save)
        .flatMap(review -> ServerResponse.status(HttpStatus.CREATED).bodyValue(review));
  }

  private void validate(Review review) {
    Set<ConstraintViolation<Review>> constraintViolations = validator.validate(review);
    log.info("ConstraintViolation: {}", constraintViolations);
    if (constraintViolations.size() > 0) {
      String errorMessage = constraintViolations.stream()
          .map(ConstraintViolation::getMessage)
          .sorted()
          .collect(joining(","));

      throw new ReviewDataException(errorMessage);
    }
  }

  public Mono<ServerResponse> getReviews(ServerRequest request) {
    Flux<Review> reviews = request.queryParam("movieInfoId")
        .map(movieInfoId -> reviewReactiveRepository.findAllByMovieInfoId(Long.valueOf(movieInfoId)))
        .orElseGet(reviewReactiveRepository::findAll);

    return ServerResponse.ok().body(reviews, Review.class);
  }

  public Mono<ServerResponse> updateReview(ServerRequest request) {
    String reviewId = request.pathVariable("id");
    Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);
//        .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given review id : " + reviewId)));

    return existingReview.log()
        .flatMap(review ->
            request.bodyToMono(Review.class)
                .log()
                .map(reqReview -> {
                  review.setComment(reqReview.getComment());
                  review.setRating(reqReview.getRating());
                  return review;
                })
                .log()
                .flatMap(reviewReactiveRepository::save)
                .log()
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                .log()).log()
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> deleteReview(ServerRequest request) {
    String reviewId = request.pathVariable("id");
    Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);
    return existingReview.flatMap(review -> reviewReactiveRepository.deleteById(review.getReviewId()))
        .then(ServerResponse.noContent().build());
  }
}
