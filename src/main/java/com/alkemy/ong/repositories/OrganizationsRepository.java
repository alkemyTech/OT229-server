package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationsRepository extends JpaRepository<Organization, String> {

    public Boolean existsByName(String name);

}
