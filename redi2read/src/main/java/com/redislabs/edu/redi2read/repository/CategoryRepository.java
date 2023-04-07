package com.redislabs.edu.redi2read.repository;

import com.redislabs.edu.redi2read.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {

}
