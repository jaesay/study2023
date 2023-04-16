package com.example.springbootrediscache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringBootRedisCacheApplication implements CommandLineRunner {
  private final ItemEntityRepository itemEntityRepository;

  public static void main(String[] args) {
    SpringApplication.run(SpringBootRedisCacheApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    itemEntityRepository.save(new ItemEntity(1L, "ITEM1"));
  }
}
