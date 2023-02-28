package com.optimagrowth.organization.repository;

import com.optimagrowth.organization.model.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationJpaEntity, String> {

}
