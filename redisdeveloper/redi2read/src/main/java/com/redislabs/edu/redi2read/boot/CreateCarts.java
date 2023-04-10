package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.model.Book;
import com.redislabs.edu.redi2read.model.Cart;
import com.redislabs.edu.redi2read.model.CartItem;
import com.redislabs.edu.redi2read.model.User;
import com.redislabs.edu.redi2read.repository.BookRepository;
import com.redislabs.edu.redi2read.repository.CartRepository;
import com.redislabs.edu.redi2read.service.CartService;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(5)
@Slf4j
public class CreateCarts implements CommandLineRunner {

  private final RedisTemplate<String, String> redisTemplate;
  private final CartRepository cartRepository;
  private final BookRepository bookRepository;
  private final CartService cartService;
  private final Integer numberOfCarts;

  public CreateCarts(RedisTemplate<String, String> redisTemplate, CartRepository cartRepository,
      BookRepository bookRepository, CartService cartService,
      @Value("${app.numberOfCarts}") Integer numberOfCarts) {

    this.redisTemplate = redisTemplate;
    this.cartRepository = cartRepository;
    this.bookRepository = bookRepository;
    this.cartService = cartService;
    this.numberOfCarts = numberOfCarts;
  }

  @Override
  public void run(String... args) throws Exception {
    if (cartRepository.count() == 0) {
      Random random = new Random();

      // loops for the number of carts to create
      IntStream.range(0, numberOfCarts).forEach(n -> {
        // get a random user
        String userId = redisTemplate.opsForSet()//
            .randomMember(User.class.getName());

        // make a cart for the user
        Cart cart = Cart.builder()
            .userId(userId)
            .build();

        // get between 1 and 7 books
        Set<Book> books = getRandomBooks(bookRepository, 7);

        // add to cart
        cart.setCartItems(getCartItemsForBooks(books));

        // save the cart
        cartRepository.save(cart);

        // randomly checkout carts
        if (random.nextBoolean()) {
          cartService.checkout(cart.getId());
        }
      });

      log.info(">>>> Created Carts...");
    }
  }

  private Set<Book> getRandomBooks(BookRepository bookRepository, int max) {
    Random random = new Random();
    int howMany = random.nextInt(max) + 1;
    Set<Book> books = new HashSet<>();
    IntStream.range(1, howMany).forEach(n -> {
      String randomBookId = redisTemplate.opsForSet().randomMember(Book.class.getName());
      books.add(bookRepository.findById(randomBookId).get());
    });

    return books;
  }

  private Set<CartItem> getCartItemsForBooks(Set<Book> books) {
    Set<CartItem> items = new HashSet<>();
    books.forEach(book -> {
      CartItem item = CartItem.builder()
          .isbn(book.getId())
          .price(book.getPrice())
          .quantity(1L)
          .build();
      items.add(item);
    });

    return items;
  }
}
