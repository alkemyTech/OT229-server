package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationsRepository extends JpaRepository<Organization, String> {
}
