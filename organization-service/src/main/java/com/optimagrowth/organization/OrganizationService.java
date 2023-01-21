package com.optimagrowth.organization;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationRepository repository;

  public Organization findById(String organizationId) {
    return repository.findById(organizationId)
        .map(Organization::from)
        .orElse(null);
  }

  public Organization create(Organization organization){
    organization.setId( UUID.randomUUID().toString());
    repository.save(OrganizationEntity.from(organization));
    return organization;
  }

  public void update(Organization organization){
    repository.save(OrganizationEntity.from(organization));
  }

  public void delete(Organization organization){
    repository.deleteById(organization.getId());
  }
}
