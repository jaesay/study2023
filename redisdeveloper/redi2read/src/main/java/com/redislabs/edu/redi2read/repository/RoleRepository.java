package com.redislabs.edu.redi2read.repository;

import com.redislabs.edu.redi2read.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {

  Role findFirstByName(String role);
}
