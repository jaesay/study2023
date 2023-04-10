package com.redislabs.edu.redi2read.service;

import com.redislabs.edu.redi2read.model.Book;
import com.redislabs.edu.redi2read.model.Cart;
import com.redislabs.edu.redi2read.model.CartItem;
import com.redislabs.edu.redi2read.model.User;
import com.redislabs.edu.redi2read.repository.BookRepository;
import com.redislabs.edu.redi2read.repository.CartRepository;
import com.redislabs.edu.redi2read.repository.UserRepository;
import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.LongStream;
import org.springframework.stereotype.Service;

@Service
public class CartService {

  private final CartRepository cartRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final JReJSON redisJson;
  private final Path cartItemsPath;

  public CartService(CartRepository cartRepository, BookRepository bookRepository,
      UserRepository userRepository) {
    this.cartRepository = cartRepository;
    this.bookRepository = bookRepository;
    this.userRepository = userRepository;
    this.redisJson = new JReJSON();
    this.cartItemsPath = Path.of(".cartItems");
  }

  public Cart get(String id) {
    return cartRepository.findById(id).get();
  }

  public void addToCart(String id, CartItem item) {
    Optional<Book> book = bookRepository.findById(item.getIsbn());
    if (book.isPresent()) {
      String cartKey = CartRepository.getKey(id);
      item.setPrice(book.get().getPrice());
      redisJson.arrAppend(cartKey, cartItemsPath, item);
    }
  }

  public void removeFromCart(String id, String isbn) {
    Optional<Cart> cartFinder = cartRepository.findById(id);
    if (cartFinder.isPresent()) {
      Cart cart = cartFinder.get();
      String cartKey = CartRepository.getKey(cart.getId());
      List<CartItem> cartItems = new ArrayList<CartItem>(cart.getCartItems());
      OptionalLong cartItemIndex =  LongStream.range(0, cartItems.size()).filter(i -> cartItems.get((int) i).getIsbn().equals(isbn)).findFirst();
      if (cartItemIndex.isPresent()) {
        redisJson.arrPop(cartKey, CartItem.class, cartItemsPath, cartItemIndex.getAsLong());
      }
    }
  }

  public void checkout(String id) {
    Cart cart = cartRepository.findById(id).get();
    User user = userRepository.findById(cart.getUserId()).get();
    cart.getCartItems().forEach(cartItem -> {
      Book book = bookRepository.findById(cartItem.getIsbn()).get();
      user.addBook(book);
    });
    userRepository.save(user);
    // cartRepository.delete(cart);
  }
}
