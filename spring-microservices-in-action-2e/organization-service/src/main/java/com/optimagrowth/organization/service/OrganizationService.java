package com.optimagrowth.organization.service;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.organization.events.SimpleSourceBean;
import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.model.OrganizationMapper;
import com.optimagrowth.organization.repository.OrganizationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationRepository repository;
  private final SimpleSourceBean simpleSourceBean;
  private final OrganizationMapper mapper;
  private final Tracer tracer;

  public Organization findById(String organizationId) {
    ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");
    try {
      return repository.findById(organizationId)
          .map(mapper::from)
          .orElseThrow(() -> new IllegalArgumentException(
              String.format("Unable to find an organization with the Organization id %s",
                  organizationId))
          );

    } finally {
      newSpan.tag("peer.service", "postgres");
      newSpan.annotate("Client received");
      newSpan.finish();
    }
  }

  public Organization create(Organization organization){
    organization.setId( UUID.randomUUID().toString());
    repository.save(mapper.from(organization));
    simpleSourceBean.publishOrganizationChange("SAVE", organization.getId());
    return organization;
  }

  public void update(Organization organization){
    repository.save(mapper.from(organization));
  }

  public void delete(Organization organization){
    repository.deleteById(organization.getId());
  }
}
