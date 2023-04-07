package com.redislabs.edu.redi2read.repository;

import com.redislabs.edu.redi2read.model.BookRating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRatingRepository extends CrudRepository<BookRating, String> {

}
