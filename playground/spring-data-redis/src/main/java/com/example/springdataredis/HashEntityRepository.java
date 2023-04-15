package com.example.springdataredis;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

interface HashEntityRepository extends CrudRepository<HashEntity, Long> {

  Optional<HashEntity> findByStr(String str);
}
