package com.springreactive.moviesinfoservice.boot;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import com.springreactive.moviesinfoservice.repository.MovieInfoRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadMovieInfoEventListener implements ApplicationListener<ApplicationReadyEvent> {

  private final MovieInfoRepository repository;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    repository.saveAll(
            List.of(
                new MovieInfo(
                    "1",
                    "Batman Begins",
                    2005,
                    List.of("Christian Bale", "Michael Cane"),
                    LocalDate.of(2005, 6, 15)),
                new MovieInfo(
                    "2",
                    "The Dark Knight",
                    2008,
                    List.of("Christian Bale", "HeathLedger"),
                    LocalDate.of(2008, 7, 18)),
                new MovieInfo(
                    "3",
                    "Dark Knight Rises",
                    2012,
                    List.of("Christian Bale", "Tom Hardy"),
                    LocalDate.of(2012, 7, 20))))
        .subscribe();
  }
}
