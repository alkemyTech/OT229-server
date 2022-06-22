package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.mappers.OrganizationMapper;
import com.alkemy.ong.repositories.OrganizationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService{

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private OrganizationsRepository organizationsRepository;


    public List<OrganizationDTO> getAll() {
        List<Organization> entities = organizationsRepository.findAll();
        List<OrganizationDTO> dtos = organizationMapper.organizationEntity2DTOList(entities);

        return dtos;
    }


    public OrganizationDTO getById(String id) {
        Optional<Organization> entity = organizationsRepository.findById(id);
        if (!entity.isPresent()) {
            throw new RuntimeException("Organization with the provided ID not present");
        }
        OrganizationDTO dto = organizationMapper.organizationEntity2DTO(entity.get());
        return dto;
    }
}
