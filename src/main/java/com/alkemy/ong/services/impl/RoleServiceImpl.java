package com.alkemy.ong.services.impl;

import com.alkemy.ong.entities.Role;
import com.alkemy.ong.repositories.RoleRepository;
import com.alkemy.ong.services.RoleService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Set<Role> findRoleUser() {
        // Rol: ROLE_USER
        Set<Role> roles = new LinkedHashSet<>();
        roles.add(roleRepository.findByName(GlobalConstants.ROLE_USER).get());

        return roles;
    }
}
