package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
public class Organization extends RepresentationModel<Organization> {

  private String id;
  private String name;
  private String contactName;
  private String contactEmail;
  private String contactPhone;

  public static Organization from(OrganizationRedisEntity entity) {
    Organization organization = new Organization();
    organization.id = entity.getId();
    organization.name = entity.getName();
    organization.contactName = entity.getContactName();
    organization.contactEmail = entity.getContactEmail();
    organization.contactPhone = entity.getContactPhone();
    return organization;
  }
}
