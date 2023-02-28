package com.optimagrowth.organization.repository;

import com.optimagrowth.organization.model.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, String> {

}
