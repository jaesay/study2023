package com.redislabs.edu.redi2read.repository;

import com.redislabs.edu.redi2read.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
  User findFirstByEmail(String email);
}
