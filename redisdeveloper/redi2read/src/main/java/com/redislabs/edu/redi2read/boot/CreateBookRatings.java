package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.model.Book;
import com.redislabs.edu.redi2read.model.BookRating;
import com.redislabs.edu.redi2read.model.User;
import com.redislabs.edu.redi2read.repository.BookRatingRepository;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@Slf4j
public class CreateBookRatings implements CommandLineRunner {

  private final Integer numberOfRatings;
  private final Integer ratingStars;
  private final RedisTemplate<String, String> redisTemplate;
  private final BookRatingRepository bookRatingRepository;

  public CreateBookRatings(@Value("${app.numberOfRatings}") Integer numberOfRatings,
      @Value("${app.ratingStars}") Integer ratingStars,
      RedisTemplate<String, String> redisTemplate, BookRatingRepository bookRatingRepository) {

    this.numberOfRatings = numberOfRatings;
    this.ratingStars = ratingStars;
    this.redisTemplate = redisTemplate;
    this.bookRatingRepository = bookRatingRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (bookRatingRepository.count() == 0) {
      Random random = new Random();
      IntStream.range(0, numberOfRatings).forEach(n -> {
        String bookId = redisTemplate.opsForSet().randomMember(Book.class.getName());
        String userId = redisTemplate.opsForSet().randomMember(User.class.getName());
        int stars = random.nextInt(ratingStars) + 1;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);

        BookRating rating = BookRating.builder() //
            .user(user) //
            .book(book) //
            .rating(stars).build();
        bookRatingRepository.save(rating);
      });

      log.info(">>>> BookRating created...");
    }
  }
}
