package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;

import java.util.List;

public interface OrganizationService {

    public List<OrganizationDTO> getAll();

    public OrganizationDTO getById (String id);
}
