package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
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

    @Override
    public List<ReducedOrganizationDTO> getAll() {
        List<Organization> entities = organizationsRepository.findAll();
        List<ReducedOrganizationDTO> dtos = organizationMapper.organizationEntity2ReducedDTOList(entities);

        return dtos;
    }

    @Override
    public ReducedOrganizationDTO getById(String id) {
        Optional<Organization> entity = organizationsRepository.findById(id);
        if (!entity.isPresent()) {
            throw new RuntimeException("Organization with the provided ID not present");
        }
        ReducedOrganizationDTO dto = organizationMapper.organizationEntity2ReducedDTO(entity.get());
        return dto;
    }

    @Override
    public OrganizationDTO updateOrganization(OrganizationDTO organizationDTO, String organizationName) {
        Organization organization = organizationsRepository.findByName(organizationName).get();

        organization = updateInfo(organization, organizationDTO);

        organizationsRepository.save(organization);

        return organizationMapper.organizationDTO2Entity(organization);
    }

    private Organization updateInfo(Organization organization, OrganizationDTO organizationDTO){
        // Si el atributo del objeto OrganizationDTO es null, es porque no se quiere actualizar
        if(organizationDTO.getName() != null && !organization.getName().equalsIgnoreCase(organizationDTO.getName())){
            organization.setName(organizationDTO.getName());
        }
        if(organizationDTO.getImage() != null && !organization.getImage().equalsIgnoreCase(organizationDTO.getImage())){
            organization.setImage(organizationDTO.getImage());
        }
        if(organizationDTO.getPhone() != null && organization.getPhone() != organizationDTO.getPhone()){
            organization.setPhone(organizationDTO.getPhone());
        }
        if(organizationDTO.getAddress() != null && !organization.getAddress().equalsIgnoreCase(organizationDTO.getAddress())){
            organization.setAddress(organizationDTO.getAddress());
        }
        if(organizationDTO.getEmail() != null && !organization.getEmail().equalsIgnoreCase(organizationDTO.getEmail())){
            organization.setEmail(organizationDTO.getEmail());
        }
        if(organizationDTO.getWelcomeText() != null && !organization.getWelcomeText().equalsIgnoreCase(organizationDTO.getWelcomeText())){
            organization.setWelcomeText(organizationDTO.getWelcomeText());
        }
        if(organizationDTO.getAboutUsText() != null && !organization.getAboutUsText().equalsIgnoreCase(organizationDTO.getAboutUsText())){
            organization.setAboutUsText(organizationDTO.getAboutUsText());
        }

        return organization;
    }
}
