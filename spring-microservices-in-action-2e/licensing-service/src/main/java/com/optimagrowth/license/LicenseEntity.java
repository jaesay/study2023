package com.optimagrowth.license;

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
public class LicenseEntity {
  @Id
  private String licenseId;
  private String description;
  private String organizationId;
  private String productName;
  private String licenseType;

  public static LicenseEntity from(License license) {
    LicenseEntity entity = new LicenseEntity();
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
    LicenseEntity that = (LicenseEntity) o;
    return licenseId != null && Objects.equals(licenseId, that.licenseId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
