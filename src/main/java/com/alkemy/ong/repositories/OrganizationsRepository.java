package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationsRepository extends JpaRepository<Organization, String> {
}
