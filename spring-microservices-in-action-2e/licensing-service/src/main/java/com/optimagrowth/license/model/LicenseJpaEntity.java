package com.optimagrowth.license.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

@Entity(name = "licenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LicenseJpaEntity {
  @Id
  private String licenseId;
  private String description;
  private String organizationId;
  private String productName;
  private String licenseType;

  public static LicenseJpaEntity from(License license) {
    LicenseJpaEntity entity = new LicenseJpaEntity();
    entity.licenseId = license.getLicenseId();
    entity.description = license.getDescription();
    entity.organizationId = license.getOrganizationId();
    entity.productName = license.getProductName();
    entity.licenseType = license.getLicenseType();
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    LicenseJpaEntity that = (LicenseJpaEntity) o;
    return licenseId != null && Objects.equals(licenseId, that.licenseId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
