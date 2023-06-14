package com.example.webfluxpatterns.aggregator.service;

import com.example.webfluxpatterns.aggregator.client.ProductClient;
import com.example.webfluxpatterns.aggregator.client.PromotionClient;
import com.example.webfluxpatterns.aggregator.client.ReviewClient;
import com.example.webfluxpatterns.aggregator.dto.ProductAggregate;
import com.example.webfluxpatterns.aggregator.dto.ProductAggregate.Price;
import com.example.webfluxpatterns.aggregator.dto.ProductResponse;
import com.example.webfluxpatterns.aggregator.dto.PromotionResponse;
import com.example.webfluxpatterns.aggregator.dto.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAggregateService {

  private final ProductClient productClient;
  private final PromotionClient promotionClient;
  private final ReviewClient reviewClient;

  public Mono<ProductAggregate> aggregate(long productId) {
    return Mono.zip(
            productClient.getProduct(productId),
            promotionClient.getPromotion(productId),
            reviewClient.getReviews(productId)
        )
        .log()
        .map(t -> toDto(t.getT1(), t.getT2(), t.getT3()))
        .log();
  }

  private ProductAggregate toDto(ProductResponse product, PromotionResponse promotion, List<Review> reviews) {
    var price = new Price();
    var amountSaved = product.getPrice() * promotion.getDiscount() / 100;
    var discountedPrice = product.getPrice() - amountSaved;
    price.setListPrice(product.getPrice());
    price.setAmountSaved(amountSaved);
    price.setDiscountedPrice(discountedPrice);
    price.setDiscount(promotion.getDiscount());
    price.setEndDate(promotion.getEndDate());

    return ProductAggregate.create(product.getId(), product.getCategory(), product.getDescription(), price, reviews);
  }

}
