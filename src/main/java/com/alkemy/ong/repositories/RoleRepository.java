package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByName(String roleName);

    Boolean existsByName(String name);
}
