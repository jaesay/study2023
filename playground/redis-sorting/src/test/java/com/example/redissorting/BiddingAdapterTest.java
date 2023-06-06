package com.example.redissorting;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BiddingAdapterTest {

  private static final Long FIRST_USER_ID = 1L;
  private static final Long SECOND_USER_ID = 2L;
  private static final Long THIRD_USER_ID = 3L;
  private static final Long FOURTH_USER_ID = 4L;
  private static final Long FIFTH_USER_ID = 5L;
  private static final Long HOTEL_ID = 1000L;

  @Autowired
  private BiddingAdapter biddingAdapter;

  @Container
  static final RedisContainer redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

  @DynamicPropertySource
  public static void setUpContainers(DynamicPropertyRegistry registry) {
    redis.start();

    registry.add("spring.redis.host", redis::getHost);
    registry.add("spring.redis.port", redis::getFirstMappedPort);
  }

  @Test
  void simulate() {
    biddingAdapter.clear(HOTEL_ID);

    biddingAdapter.createBidding(HOTEL_ID, FIRST_USER_ID, 100d);
    biddingAdapter.createBidding(HOTEL_ID, SECOND_USER_ID, 110d);
    biddingAdapter.createBidding(HOTEL_ID, THIRD_USER_ID, 120d);
    biddingAdapter.createBidding(HOTEL_ID, FOURTH_USER_ID, 130d);
    biddingAdapter.createBidding(HOTEL_ID, FIFTH_USER_ID, 140d);

    biddingAdapter.createBidding(HOTEL_ID, SECOND_USER_ID, 150d);
    biddingAdapter.createBidding(HOTEL_ID, FIRST_USER_ID, 200d);

    List<Long> topBidders = biddingAdapter.getTopBidders(HOTEL_ID, 3);

    assertThat(topBidders).containsExactly(FIRST_USER_ID, SECOND_USER_ID, FIFTH_USER_ID);
    assertThat(biddingAdapter.getBidAmount(HOTEL_ID, FIRST_USER_ID)).isEqualTo(200d);
    assertThat(biddingAdapter.getBidAmount(HOTEL_ID, SECOND_USER_ID)).isEqualTo(150d);
    assertThat(biddingAdapter.getBidAmount(HOTEL_ID, FIFTH_USER_ID)).isEqualTo(140d);
  }
}