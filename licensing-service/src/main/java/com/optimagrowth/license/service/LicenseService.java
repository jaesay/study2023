package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.LicenseEntity;
import com.optimagrowth.license.repository.LicenseRepository;
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
