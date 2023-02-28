package com.optimagrowth.license.model;

import javax.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("organization")
public class OrganizationRedisEntity {

  @Id
  private String id;
  private String name;
  private String contactName;
  private String contactEmail;
  private String contactPhone;
}
