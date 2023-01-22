package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.LicenseEntity;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LicenseService {

  private final MessageSource messages;
  private final LicenseRepository licenseRepository;
  private final ServiceConfig config;
  private final OrganizationFeignClient organizationFeignClient;
  private final OrganizationRestTemplateClient organizationRestClient;
  private final OrganizationDiscoveryClient organizationDiscoveryClient;

  public License getLicense(String licenseId, String organizationId, String clientType){
    License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
        .map(License::from)
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            messages.getMessage("license.search.error.message",
                new String[]{licenseId, organizationId}, Locale.getDefault()), licenseId,
            organizationId)));

    Organization organization = retrieveOrganizationInfo(organizationId, clientType);
    license.setOrganization(organization);

    return license.withComment(config.getProperty());
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

    return license.withComment(config.getProperty());
  }

  public License createLicense(License license) {
    license.setLicenseId(UUID.randomUUID().toString());
    licenseRepository.save(LicenseEntity.from(license));
    return license.withComment(config.getProperty());
  }

  public License updateLicense(License license) {
    licenseRepository.save(LicenseEntity.from(license));
    return license.withComment(config.getProperty());
  }

  public String deleteLicense(String licenseId) {
    licenseRepository.deleteById(licenseId);
    return String.format(
        messages.getMessage("license.delete.message", new String[]{licenseId}, Locale.getDefault()),
        licenseId);
  }
}
