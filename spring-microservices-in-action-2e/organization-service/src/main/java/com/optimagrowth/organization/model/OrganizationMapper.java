package com.optimagrowth.organization.model;

import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

  public Organization from(OrganizationJpaEntity entity) {
    Organization organization = new Organization();
    organization.setId(entity.getId());
    organization.setName(entity.getName());
    organization.setContactName(entity.getContactName());
    organization.setContactEmail(entity.getContactEmail());
    organization.setContactPhone(entity.getContactPhone());
    return organization;
  }

  public OrganizationJpaEntity from(Organization organization) {
    OrganizationJpaEntity entity = new OrganizationJpaEntity();
    entity.setId(organization.getId());
    entity.setName(organization.getName());
    entity.setContactName(organization.getContactName());
    entity.setContactEmail(organization.getContactEmail());
    entity.setContactPhone(organization.getContactPhone());
    return entity;
  }
}
