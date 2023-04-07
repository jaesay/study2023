package com.redislabs.edu.redi2read.repository;

import com.redislabs.edu.redi2read.model.Book;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, String> {

}
