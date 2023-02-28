package com.optimagrowth.organization.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Table(name = "organizations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @ToString
public class OrganizationEntity {

  @Id
  @Column(name = "organization_id", nullable = false)
  private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "contact_name", nullable = false)
  private String contactName;

  @Column(name = "contact_email", nullable = false)
  private String contactEmail;

  @Column(name = "contact_phone", nullable = false)
  private String contactPhone;

  public static OrganizationEntity from(Organization organization) {
    OrganizationEntity entity = new OrganizationEntity();
    entity.id = organization.getId();
    entity.name = organization.getName();
    entity.contactName = organization.getContactName();
    entity.contactEmail = organization.getContactEmail();
    entity.contactPhone = organization.getContactPhone();
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
    OrganizationEntity that = (OrganizationEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
