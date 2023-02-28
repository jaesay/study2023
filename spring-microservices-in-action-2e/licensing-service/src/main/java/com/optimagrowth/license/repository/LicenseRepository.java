package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.LicenseJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<LicenseJpaEntity, String> {

  List<LicenseJpaEntity> findByOrganizationId(String organizationId);
  Optional<LicenseJpaEntity> findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

}
