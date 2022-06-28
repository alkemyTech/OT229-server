package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrganizationService {

    public List<ReducedOrganizationDTO> getAll();

    public ReducedOrganizationDTO getById (String id);

    public OrganizationDTO updateOrganization(MultipartFile image, OrganizationDTO organizationDTO) throws IOException;
}
