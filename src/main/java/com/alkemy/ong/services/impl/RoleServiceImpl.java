package com.alkemy.ong.services.impl;

import com.alkemy.ong.entities.Role;
import com.alkemy.ong.repositories.RoleRepository;
import com.alkemy.ong.services.RoleService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public Set<Role> getRoleUser() {
        // Rol: ROLE_USER
        Optional<Role> roleFound = roleRepository.findByName(GlobalConstants.ROLE_USER);
        Set<Role> roles = new LinkedHashSet<>();

        if(roleFound.isPresent()){
            roles.add(roleFound.get());
        }else{
            Role role = creationRoleUser();
            roles.add(role);
        }

        return roles;
    }

    @Override
    public Role creationRoleUser(){
        Role role = new Role();

        role.setName(GlobalConstants.ROLE_USER);
        role.setDescription("This rol is for the common user.");

        roleRepository.save(role);

        return role;
    }
}
