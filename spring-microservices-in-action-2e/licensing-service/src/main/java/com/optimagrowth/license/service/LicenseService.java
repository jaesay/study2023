package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.LicenseEntity;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.client.OrganizationFeignClient;
import com.optimagrowth.license.client.OrganizationRestTemplateClient;
import com.optimagrowth.license.config.CommentConfigProps;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseService {

  private final MessageSource messages;
  private final LicenseRepository licenseRepository;
  private final CommentConfigProps commentConfigProps;
  private final OrganizationFeignClient organizationFeignClient;
  private final OrganizationRestTemplateClient organizationRestClient;
  private final OrganizationDiscoveryClient organizationDiscoveryClient;

  @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
  @Bulkhead(name = "bulkheadLicenseService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList")
  @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
  @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
  public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
    log.debug("LicenseService#getLicensesByOrganization > Thread Name: {}, Correlation Id: {}", Thread.currentThread().getName(), UserContextHolder.getContext().getCorrelationId());
    randomlyRunLong();
    return licenseRepository.findByOrganizationId(organizationId)
        .stream().map(License::from)
        .collect(Collectors.toList());
  }

  private List<License> buildFallbackLicenseList(String organizationId, Throwable t) {
    log.debug("LicenseService#buildFallbackLicenseList > Thread Name: {}, Correlation Id: {}", Thread.currentThread().getName(), UserContextHolder.getContext().getCorrelationId());
    log.error(t.getMessage());
    return List.of(License.createUnavailableLicense(organizationId));
  }

  private void randomlyRunLong() throws TimeoutException {
    Random rand = new Random();
    int randomNum = rand.nextInt(3) + 1;
    if (randomNum == 3) sleep();
  }

  private void sleep() throws TimeoutException {
    try {
      Thread.sleep(5000);
      throw new TimeoutException();
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }

  public License getLicense(String licenseId, String organizationId, String clientType){
    License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
        .map(License::from)
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            messages.getMessage("license.search.error.message",
                new String[]{licenseId, organizationId}, Locale.getDefault()), licenseId,
            organizationId)));

    Organization organization = retrieveOrganizationInfo(organizationId, clientType);
    license.setOrganization(organization);

    return license.withComment(commentConfigProps.getComment());
  }

  private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
    Organization organization = null;

    switch (clientType) {
      case "feign":
        System.out.println("I am using the feign client");
        organization = organizationFeignClient.getOrganization(organizationId);
        break;
      case "rest":
        System.out.println("I am using the rest client");
        organization = organizationRestClient.getOrganization(organizationId);
        break;
      case "discovery":
        System.out.println("I am using the discovery client");
        organization = organizationDiscoveryClient.getOrganization(organizationId);
        break;
      default:
        organization = organizationRestClient.getOrganization(organizationId);
        break;
    }

    return organization;
  }

  public License getLicense(String licenseId, String organizationId) {
    License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId,
            licenseId)
        .map(License::from)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format(
                messages.getMessage("license.search.error.message",
                    new String[]{licenseId, organizationId}, Locale.getDefault()),
                licenseId, organizationId)));

    return license.withComment(commentConfigProps.getComment());
  }

  public License createLicense(License license) {
    license.setLicenseId(UUID.randomUUID().toString());
    licenseRepository.save(LicenseEntity.from(license));
    return license.withComment(commentConfigProps.getComment());
  }

  public License updateLicense(License license) {
    licenseRepository.save(LicenseEntity.from(license));
    return license.withComment(commentConfigProps.getComment());
  }

  public String deleteLicense(String licenseId) {
    licenseRepository.deleteById(licenseId);
    return String.format(
        messages.getMessage("license.delete.message", new String[]{licenseId}, Locale.getDefault()),
        licenseId);
  }
}
