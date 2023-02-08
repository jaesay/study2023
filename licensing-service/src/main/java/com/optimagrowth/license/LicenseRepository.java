package com.optimagrowth.license;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<LicenseEntity, String> {

  List<LicenseEntity> findByOrganizationId(String organizationId);
  Optional<LicenseEntity> findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

}
