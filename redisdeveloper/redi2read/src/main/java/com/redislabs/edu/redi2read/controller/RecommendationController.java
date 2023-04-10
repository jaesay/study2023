package com.redislabs.edu.redi2read.controller;

import com.redislabs.edu.redi2read.service.RecommendationService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;

  @GetMapping("/user/{userId}")
  public Set<String> userRecommendations(@PathVariable("userId") String userId) {
    return recommendationService.getBookRecommendationsFromCommonPurchasesForUser(userId);
  }

  @GetMapping("/isbn/{isbn}/pairings")
  public Set<String> frequentPairings(@PathVariable("isbn") String isbn) {
    return recommendationService.getFrequentlyBoughtTogether(isbn);
  }
}
