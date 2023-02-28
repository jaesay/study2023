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

  public static OrganizationRedisEntity from(Organization organization) {
    OrganizationRedisEntity entity = new OrganizationRedisEntity();
    entity.id = organization.getId();
    entity.name = organization.getName();
    entity.contactName = organization.getContactName();
    entity.contactEmail = organization.getContactEmail();
    entity.contactPhone = organization.getContactPhone();
    return entity;
  }
}
