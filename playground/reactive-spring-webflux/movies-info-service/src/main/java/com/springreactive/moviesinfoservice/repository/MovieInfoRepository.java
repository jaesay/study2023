package com.springreactive.moviesinfoservice.repository;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

  Flux<MovieInfo> findByYear(int year);
}
