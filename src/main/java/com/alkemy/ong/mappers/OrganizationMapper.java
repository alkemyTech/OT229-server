package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.entities.Organization;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrganizationMapper {

    public List<ReducedOrganizationDTO> organizationEntity2ReducedDTOList(List<Organization> entities) {
        List<ReducedOrganizationDTO> dtos = new ArrayList<>();
        for (Organization o: entities) {
            dtos.add(organizationEntity2ReducedDTO(o));
        }
        return dtos;
    }

    public ReducedOrganizationDTO organizationEntity2ReducedDTO(Organization entity) {
        ReducedOrganizationDTO dto = new ReducedOrganizationDTO();

        dto.setAddress(entity.getAddress());
        dto.setImage(entity.getImage());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());

        return dto;
    }

    public OrganizationDTO organizationEntity2OrganizationDTO(Organization organization){
        OrganizationDTO organizationDTO = new OrganizationDTO();

        organizationDTO.setName(organization.getName());
        organizationDTO.setImage(organization.getImage());
        organizationDTO.setPhone(organization.getPhone());
        organizationDTO.setAddress(organization.getAddress());
        organizationDTO.setEmail(organization.getEmail());
        organizationDTO.setWelcomeText(organization.getWelcomeText());
        organizationDTO.setAboutUsText(organization.getAboutUsText());

        return organizationDTO;
    }
}
