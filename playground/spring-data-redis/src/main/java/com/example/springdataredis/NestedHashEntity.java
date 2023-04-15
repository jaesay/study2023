package com.example.springdataredis;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

@Data
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
@Builder
@RedisHash
class NestedHashEntity {

  @Id
  private Long id;

  private String str;

  @Reference
  private List<NestedNestedHashEntity> nestedNestedHashEntities;
}
