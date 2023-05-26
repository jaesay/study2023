package com.springreactive.moviesinfoservice.controller;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import com.springreactive.moviesinfoservice.service.MovieInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MovieInfoController {

  private final MovieInfoService movieInfoService;

  @GetMapping("/movieinfos")
  public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year) {
    if (year != null) {
      return movieInfoService.getMovieInfosByYear(year).log();
    }
    return movieInfoService.getAllMovieInfos().log();
  }

  @GetMapping("/movieinfos/{id}")
  public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
    return movieInfoService.getMovieInfoById(id)
        .map(ResponseEntity.ok()::body)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
        .log();
  }

  @PostMapping("/movieinfos")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
    return movieInfoService.addMovieInfo(movieInfo).log();
  }

  @PutMapping("/movieinfos/{id}")
  public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo movieInfo, @PathVariable String id) {
    return movieInfoService.updateMovieInfo(movieInfo, id)
        .map(ResponseEntity.ok()::body)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
        .log();
  }

  @DeleteMapping("/movieinfos/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteMovieInfo(@PathVariable String id) {
    return movieInfoService.deleteMovieInfo(id).log();
  }
}
