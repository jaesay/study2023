package com.reactivespring.boot;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadReviewEventListener implements ApplicationListener<ApplicationReadyEvent> {

  private final ReviewReactiveRepository repository;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    repository.saveAll(
            List.of(
                new Review("1", 1L, "Excellent Movie", 8.0),
                new Review("2", 2L, "Awesome Movie", 9.0)
            ))
        .subscribe();
  }
}
