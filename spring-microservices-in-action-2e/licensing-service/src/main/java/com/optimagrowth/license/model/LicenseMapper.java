package com.optimagrowth.license.model;

import org.springframework.stereotype.Component;

@Component
public class LicenseMapper {

  public License from(LicenseJpaEntity entity) {
    License license = new License();
    license.setLicenseId(entity.getLicenseId());
    license.setDescription(entity.getDescription());
    license.setOrganizationId(entity.getOrganizationId());
    license.setProductName(entity.getProductName());
    license.setLicenseType(entity.getLicenseType());
    return license;
  }
}
