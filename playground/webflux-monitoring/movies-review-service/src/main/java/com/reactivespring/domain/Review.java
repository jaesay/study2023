package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

    @Id
    private String reviewId;
    @Positive(message = "review.movieInfoId must be a positive value")
    private long movieInfoId;
    private String comment;
    @Min(value = 0L, message = "review.rating must be a non-negative value")
    private Double rating;
}
