package com.example.springdataredis.hash;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash
class NestedNestedHashEntity {

  @Id
  private Long id;

  private String str;
}
