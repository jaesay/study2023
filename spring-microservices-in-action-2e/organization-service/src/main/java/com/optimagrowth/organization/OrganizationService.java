package com.optimagrowth.organization;

import com.optimagrowth.organization.events.SimpleSourceBean;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationRepository repository;
  private final SimpleSourceBean simpleSourceBean;

  public Organization findById(String organizationId) {
    return repository.findById(organizationId)
        .map(Organization::from)
        .orElse(null);
  }

  public Organization create(Organization organization){
    organization.setId( UUID.randomUUID().toString());
    repository.save(OrganizationEntity.from(organization));
    simpleSourceBean.publishOrganizationChange("SAVE", organization.getId());
    return organization;
  }

  public void update(Organization organization){
    repository.save(OrganizationEntity.from(organization));
  }

  public void delete(Organization organization){
    repository.deleteById(organization.getId());
  }
}
