package com.example.webfluxpatterns._09_ratelimiter.service;

import com.example.webfluxpatterns._09_ratelimiter.client.ProductClient;
import com.example.webfluxpatterns._09_ratelimiter.client.ReviewClient;
import com.example.webfluxpatterns._09_ratelimiter.dto.Product;
import com.example.webfluxpatterns._09_ratelimiter.dto.ProductAggregate;
import com.example.webfluxpatterns._09_ratelimiter.dto.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAggregateService {

  private final ProductClient productClient;
  private final ReviewClient reviewClient;

  public Mono<ProductAggregate> aggregate(long productId) {
    // zip 은 all or nothing 유사, 하나가 실패하거나 empty signal을 주면 nothing (error or empty signal)
    return Mono.zip(
            productClient.getProduct(productId),
            reviewClient.getReviews(productId)
        )
        .log()
        .map(t -> toDto(t.getT1(), t.getT2()))
        .log();
  }

  private ProductAggregate toDto(Product product, List<Review> reviews) {
    return ProductAggregate.create(
        product.getId(),
        product.getCategory(),
        product.getDescription(),
        reviews);
  }

}
