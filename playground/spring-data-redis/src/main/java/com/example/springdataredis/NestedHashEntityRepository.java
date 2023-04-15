package com.example.springdataredis;

import org.springframework.data.repository.CrudRepository;

interface NestedHashEntityRepository extends CrudRepository<NestedHashEntity, Long> {

}
