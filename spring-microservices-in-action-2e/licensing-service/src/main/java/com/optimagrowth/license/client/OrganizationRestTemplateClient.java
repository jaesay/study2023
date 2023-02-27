package com.optimagrowth.license.client;

import com.optimagrowth.license.Organization;
import com.optimagrowth.license.OrganizationRedisEntity;
import com.optimagrowth.license.OrganizationRedisRepository;
import com.optimagrowth.license.usercontext.UserContext;
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
  private final UserContext userContext;

  public Organization getOrganization(String organizationId){
    log.debug("In Licensing Service.getOrganization: {}", userContext.getCorrelationId());

    OrganizationRedisEntity entity = checkRedisCache(organizationId);

    if (entity != null) {
      Organization organization = Organization.from(entity);
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
      cacheOrganizationObject(OrganizationRedisEntity.from(organization));
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
