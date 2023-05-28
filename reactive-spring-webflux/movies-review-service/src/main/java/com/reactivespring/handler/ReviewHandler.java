package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReviewHandler {

  private final ReviewReactiveRepository reviewReactiveRepository;

  public Mono<ServerResponse> addReview(ServerRequest request) {
    return request.bodyToMono(Review.class)
        .log()
        .flatMap(reviewReactiveRepository::save)
        .log()
        .flatMap(review -> ServerResponse.status(HttpStatus.CREATED).bodyValue(review))
        .log();
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
                .log()
        ).log();
  }

  public Mono<ServerResponse> deleteReview(ServerRequest request) {
    String reviewId = request.pathVariable("id");
    Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);
    return existingReview.flatMap(review -> reviewReactiveRepository.deleteById(review.getReviewId()))
        .then(ServerResponse.noContent().build());
  }
}
