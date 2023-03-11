package com.optimagrowth.license.client;

import com.optimagrowth.license.model.Organization;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OrganizationDiscoveryClient {

  private final DiscoveryClient discoveryClient;

  public Organization getOrganization(String organizationId) {
    RestTemplate restTemplate = new RestTemplate();
    List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

    if (CollectionUtils.isEmpty(instances)) {
      return null;
    }
    String serviceUri = String.format("%s/v1/organization/%s", instances.get(0).getUri().toString(),
        organizationId);

    ResponseEntity<Organization> restExchange =
        restTemplate.exchange(
            serviceUri,
            HttpMethod.GET,
            null, Organization.class, organizationId);

    return restExchange.getBody();
  }
}