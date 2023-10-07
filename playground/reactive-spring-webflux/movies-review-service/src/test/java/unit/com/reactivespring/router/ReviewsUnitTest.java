package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
class ReviewsUnitTest {

  @MockBean
  ReviewReactiveRepository reviewReactiveRepository;

  @Autowired
  WebTestClient webTestClient;

  static final String REVIEWS_URL = "/v1/reviews";

  @Test
  void addReview() {
    Review review = new Review(null, 1L, "Awesome Movie", 9.0);
    given(reviewReactiveRepository.save(isA(Review.class)))
        .willReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

    webTestClient
        .post()
        .uri(REVIEWS_URL)
        .bodyValue(review)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Review.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          Review savedReview = movieInfoEntityExchangeResult.getResponseBody();
          assertThat(savedReview).isNotNull();
          assertThat(savedReview.getReviewId()).isEqualTo("abc");
        });
  }

  @Test
  void addReview_validation() {
    Review review = new Review(null, 0L, "Awesome Movie", -9.0);

    webTestClient
        .post()
        .uri(REVIEWS_URL)
        .bodyValue(review)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(String.class)
        .isEqualTo("review.movieInfoId must be a positive value,review.rating must be a non-negative value");
  }

  @Test
  void getReviews() {
    List<Review> reviewList = List.of(
        new Review(null, 1L, "Awesome Movie", 9.0),
        new Review(null, 1L, "Awesome Movie1", 9.0),
        new Review(null, 2L, "Excellent Movie", 8.0));

    given(reviewReactiveRepository.findAll()).willReturn(Flux.fromIterable(reviewList));

    //when
    webTestClient
        .get()
        .uri("/v1/reviews")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .value(reviews -> {
          assertThat(reviews).hasSize(3);
        });
  }

  @Test
  void updateReview() {
    Review reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
    given(reviewReactiveRepository.findById(anyString())).willReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
    given(reviewReactiveRepository.save(isA(Review.class))).willReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));

    webTestClient
        .put()
        .uri("/v1/reviews/{id}", "abc")
        .bodyValue(reviewUpdate)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Review.class)
        .consumeWith(reviewResponse -> {
          Review updatedReview = reviewResponse.getResponseBody();
          assertThat(updatedReview).isNotNull();
          assertThat(updatedReview.getRating()).isEqualTo(8.0);
          assertThat(updatedReview.getComment()).isEqualTo("Not an Awesome Movie");
        });
  }

  @Test
  void updateReview_validation() {
    Review reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
    given(reviewReactiveRepository.findById(anyString())).willReturn(Mono.empty());

    webTestClient
        .put()
        .uri("/v1/reviews/{id}", "abc")
        .bodyValue(reviewUpdate)
        .exchange()
        .expectStatus().isNotFound();
//        .expectBody(String.class)
//        .isEqualTo("Review not found for the given review id : abc");
  }

  @Test
  void deleteReview() {
    var reviewId = "abc";
    given(reviewReactiveRepository.findById(anyString())).willReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
    given(reviewReactiveRepository.deleteById(anyString())).willReturn(Mono.empty());

    webTestClient
        .delete()
        .uri("/v1/reviews/{id}", reviewId)
        .exchange()
        .expectStatus().isNoContent();
  }
}