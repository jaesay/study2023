package com.optimagrowth.organization;

import lombok.Data;

@Data
public class Organization {

  private String id;
  private String name;
  private String contactName;
  private String contactEmail;
  private String contactPhone;

  public static Organization from(OrganizationEntity entity) {
    Organization organization = new Organization();
    organization.id = entity.getId();
    organization.name = entity.getName();
    organization.contactName = entity.getContactName();
    organization.contactEmail = entity.getContactEmail();
    organization.contactPhone = entity.getContactPhone();
    return organization;
  }
}
