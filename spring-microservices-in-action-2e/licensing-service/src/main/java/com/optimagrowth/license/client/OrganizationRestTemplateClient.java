package com.optimagrowth.license.client;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.model.OrganizationMapper;
import com.optimagrowth.license.model.OrganizationRedisEntity;
import com.optimagrowth.license.repository.OrganizationRedisRepository;
import com.optimagrowth.license.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationRestTemplateClient {

  private final RestTemplate restTemplate;
  private final OrganizationRedisRepository redisRepository;
  private final OrganizationMapper mapper;

  public Organization getOrganization(String organizationId){
    log.debug("In Licensing Service.getOrganization: {}", UserContextHolder.getContext().getCorrelationId());

    OrganizationRedisEntity entity = checkRedisCache(organizationId);

    if (entity != null) {
      Organization organization = mapper.from(entity);
      log.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);
      return organization;
    }

    log.debug("Unable to locate organization from the redis cache: {}.", organizationId);

    ResponseEntity<Organization> restExchange =
        restTemplate.exchange(
            "http://gateway:8072/organization/v1/organization/{organizationId}",
            HttpMethod.GET,
            null, Organization.class, organizationId);

    /*Save the record from cache*/
    Organization organization = restExchange.getBody();
    if (organization != null) {
      cacheOrganizationObject(mapper.from(organization));
    }

    return restExchange.getBody();
  }

  private OrganizationRedisEntity checkRedisCache(String organizationId) {
    try {
      return redisRepository.findById(organizationId).orElse(null);
    } catch (Exception ex) {
      log.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", organizationId, ex);
      return null;
    }
  }

  private void cacheOrganizationObject(OrganizationRedisEntity entity) {
    try {
      redisRepository.save(entity);
    } catch (Exception ex) {
      log.error("Unable to cache organization {} in Redis. Exception {}", entity.getId(), ex);
    }
  }
}
