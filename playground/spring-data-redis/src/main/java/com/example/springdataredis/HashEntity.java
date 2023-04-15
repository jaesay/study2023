package com.example.springdataredis;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
@Builder
class HashEntity {

  @Id
  private Long id;
  private String str;
  private BigDecimal bigDecimal;
  private boolean bool;
  private LocalDate localDate;
  private EnumType enumType;
  private List<String> list;
  private Set<String> set;
  private Map<String, String> map;

  @Reference
  private List<NestedHashEntity> nestedHashEntities;

  @Reference
  @JsonIdentityReference(alwaysAsId = true)
  private Set<NestedHashEntity> nestedHashEntities2;

  public enum EnumType {
    TEST
  }
}
