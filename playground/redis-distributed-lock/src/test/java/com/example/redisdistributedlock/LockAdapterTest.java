package com.example.redisdistributedlock;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class LockAdapterTest {

  static final Long FIRST_USER_ID = 1L;
  static final Long SECOND_USER_ID = 2L;
  static final Long THIRD_USER_ID = 3L;

  @Container
  static final RedisContainer REDIS = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

  @Autowired
  LockAdapter lockAdapter;


  @DynamicPropertySource
  public static void setUpContainers(DynamicPropertyRegistry registry) {
    REDIS.start();

    registry.add("spring.redis.host", REDIS::getHost);
    registry.add("spring.redis.port", REDIS::getFirstMappedPort);
  }

  @Test
  @DisplayName("firstUserId가 락을 선점한다.")
  void testLock() {
    final Long hotelId = 123123123L;

    assertThat(lockAdapter.holdLock(hotelId, FIRST_USER_ID)).isTrue();
    assertThat(lockAdapter.holdLock(hotelId, SECOND_USER_ID)).isFalse();
    assertThat(lockAdapter.holdLock(hotelId, THIRD_USER_ID)).isFalse();
    assertThat(lockAdapter.checkLock(hotelId)).isEqualTo(FIRST_USER_ID);
  }

  @Test
  @DisplayName("3명이 동시에 락을 선점하지만 1명만 락을 잡는다.")
  void testConcurrentAccess() throws InterruptedException {

    final Long hotelId = 9999999L;
    lockAdapter.clearLock(hotelId);

    CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    new Thread(new Accessor(hotelId, FIRST_USER_ID, cyclicBarrier)).start();
    new Thread(new Accessor(hotelId, SECOND_USER_ID, cyclicBarrier)).start();
    new Thread(new Accessor(hotelId, THIRD_USER_ID, cyclicBarrier)).start();
    TimeUnit.SECONDS.sleep(1);

    Long holderId = lockAdapter.checkLock(hotelId);
    assertThat(holderId).isIn(List.of(FIRST_USER_ID, SECOND_USER_ID, THIRD_USER_ID));
    lockAdapter.clearLock(hotelId);
  }

  class Accessor implements Runnable {

    private final Long hotelId;
    private final Long userId;
    private final CyclicBarrier cyclicBarrier;

    public Accessor(Long hotelId, Long userId, CyclicBarrier cyclicBarrier) {
      this.hotelId = hotelId;
      this.userId = userId;
      this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
      try {
        cyclicBarrier.await();
        lockAdapter.holdLock(hotelId, userId);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

}