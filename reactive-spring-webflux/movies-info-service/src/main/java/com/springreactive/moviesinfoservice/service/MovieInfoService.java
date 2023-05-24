package com.springreactive.moviesinfoservice.service;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import com.springreactive.moviesinfoservice.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieInfoService {

  private final MovieInfoRepository movieInfoRepository;

  public Flux<MovieInfo> getAllMovieInfos() {
    return movieInfoRepository.findAll();
  }

  public Mono<MovieInfo> getMovieInfoById(String id) {
    return movieInfoRepository.findById(id);
  }

  public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
    return movieInfoRepository.save(movieInfo);
  }

  public Mono<MovieInfo> updateMovieInfo(MovieInfo movieInfo, String id) {
    return movieInfoRepository.findById(id)
        .flatMap(existingMovieInfo -> {
          existingMovieInfo.setCast(movieInfo.getCast());
          existingMovieInfo.setYear(movieInfo.getYear());
          existingMovieInfo.setName(movieInfo.getName());
          existingMovieInfo.setReleasedDate(movieInfo.getReleasedDate());
          return movieInfoRepository.save(existingMovieInfo);
        });
  }

  public Mono<Void> deleteMovieInfo(String id) {
    return movieInfoRepository.deleteById(id);
  }
}
