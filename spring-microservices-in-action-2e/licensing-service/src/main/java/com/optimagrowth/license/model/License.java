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

  private String organizationName;
  private String contactName;
  private String contactPhone;
  private String contactEmail;

  public static License createUnavailableLicense(String organizationId) {
    License license = new License();
    license.licenseId = "0000000-00-00000";
    license.organizationId = organizationId;
    license.productName = "Sorry no licensing information currently available";
    return license;
  }

  public License withComment(String comment){
    this.setComment(comment);
    return this;
  }

  public void setOrganization(Organization organization) {
    if (organization != null) {
      this.organizationName = organization.getName();
      this.contactName = organization.getContactName();
      this.contactEmail = organization.getContactEmail();
      this.contactPhone = organization.getContactPhone();
    }
  }
}
