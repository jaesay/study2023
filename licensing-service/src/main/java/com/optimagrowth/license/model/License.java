package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter @ToString
public class License extends RepresentationModel<License> {
  private String licenseId;
  private String description;
  private String organizationId;
  private String productName;
  private String licenseType;
  private String comment;

  public static License from(LicenseEntity entity) {
    License license = new License();
    license.licenseId = entity.getLicenseId();
    license.description = entity.getDescription();
    license.organizationId = entity.getOrganizationId();
    license.productName = entity.getProductName();
    license.licenseType = entity.getLicenseType();
    return license;
  }

  public License withComment(String comment){
    this.setComment(comment);
    return this;
  }
}
