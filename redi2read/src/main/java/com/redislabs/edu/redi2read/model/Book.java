package com.redislabs.edu.redi2read.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RedisHash
public class Book {

  @Id
  @EqualsAndHashCode.Include
  private String id;

  private String title;
  private String subtitle;
  private String description;
  private String language;
  private Long pageCount;
  private String thumbnail;
  private Double price;
  private String currency;
  private String infoLink;

  private Set<String> authors;

  @Reference
  private Set<Category> categories = new HashSet<>();

  public void addCategory(Category category) {
    categories.add(category);
  }
}
