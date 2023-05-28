package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = "spring.mongodb.embedded.version=3.2.2"
)
@ActiveProfiles("test")
class ReviewIntgTest {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  ReviewReactiveRepository reviewReactiveRepository;

  static String REVIEWS_URL = "/v1/reviews";

  @BeforeEach
  void setUp() {

    var reviewsList = List.of(
        new Review(null, 1L, "Awesome Movie", 9.0),
        new Review(null, 1L, "Awesome Movie1", 9.0),
        new Review(null, 2L, "Excellent Movie", 8.0));
    reviewReactiveRepository.saveAll(reviewsList)
        .blockLast();
  }

  @AfterEach
  void tearDown() {
    reviewReactiveRepository.deleteAll()
        .block();
  }

  @Test
  void addReview() {
    Review review = new Review(null, 1L, "Awesome Movie", 9.0);

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
          assertThat(savedReview.getReviewId()).isNotNull();
        });
  }

  @Test
  void getReviews() {

    webTestClient
        .get()
        .uri(REVIEWS_URL)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .value(reviews -> {
          assertThat(reviews).hasSize(3);
        });
  }

  @Test
  void getReviewsByMovieInfoId() {

    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(REVIEWS_URL)
            .queryParam("movieInfoId", "1")
            .build())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .value(reviews -> {
          assertThat(reviews).hasSize(2);
        });
  }

  @Test
  void updateReview() {
    var review = new Review(null, 1L, "Awesome Movie", 9.0);
    var savedReview = reviewReactiveRepository.save(review).block();
    var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);

    assertThat(savedReview).isNotNull();

    webTestClient
        .put()
        .uri(REVIEWS_URL + "/{id}", savedReview.getReviewId())
        .bodyValue(reviewUpdate)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Review.class)
        .consumeWith(reviewResponse -> {
          Review updatedReview = reviewResponse.getResponseBody();
          assertThat(updatedReview).isNotNull();
          assertThat(updatedReview.getReviewId()).isEqualTo(savedReview.getReviewId());
          assertThat(updatedReview.getRating()).isEqualTo(8.0);
          assertThat(updatedReview.getComment()).isEqualTo("Not an Awesome Movie");
        });
  }

  @Test
  void deleteReview() {
    var review = new Review(null, 1L, "Awesome Movie", 9.0);
    var savedReview = reviewReactiveRepository.save(review).block();

    assertThat(savedReview).isNotNull();

    webTestClient
        .delete()
        .uri(REVIEWS_URL + "/{id}", savedReview.getReviewId())
        .exchange()
        .expectStatus().isNoContent();
  }
}