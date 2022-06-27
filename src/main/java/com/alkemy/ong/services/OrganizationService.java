package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;

import java.util.List;

public interface OrganizationService {

    public List<ReducedOrganizationDTO> getAll();

    public ReducedOrganizationDTO getById (String id);

    public OrganizationDTO updateOrganization(OrganizationDTO organizationDTO, String organizationName);
}
