package com.springreactive.moviesinfoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {

  @Id
  private String movieInfoId;
  @NotBlank(message = "movieInfo.name must be present")
  private String name;
  @Positive(message = "movieInfo.year must be a positive value")
  private int year;
  private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;
  private LocalDate releasedDate;
}
