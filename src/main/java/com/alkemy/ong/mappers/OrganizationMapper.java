package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.entities.Organization;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrganizationMapper {

    public List<OrganizationDTO> organizationEntity2DTOList(List<Organization> entities) {
        List<OrganizationDTO> dtos = new ArrayList<>();
        for (Organization o: entities) {
            dtos.add(organizationEntity2DTO(o));
        }
        return dtos;
    }

    public OrganizationDTO organizationEntity2DTO(Organization entity) {
        OrganizationDTO dto = new OrganizationDTO();

        dto.setAddress(entity.getAddress());
        dto.setImage(entity.getImage());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());

        return dto;
    }
}
