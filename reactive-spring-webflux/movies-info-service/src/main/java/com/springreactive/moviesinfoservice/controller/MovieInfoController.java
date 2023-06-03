package com.springreactive.moviesinfoservice.controller;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import com.springreactive.moviesinfoservice.service.MovieInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MovieInfoController {

  private final MovieInfoService movieInfoService;
  private final Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().latest();

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

  @GetMapping(value = "/movieinfos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
  public Flux<MovieInfo> streamMovieInfos() {
    return movieInfoSink.asFlux().log();
  }

  @PostMapping("/movieinfos")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
    return movieInfoService.addMovieInfo(movieInfo)
        .doOnNext(movieInfoSink::tryEmitNext);
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
