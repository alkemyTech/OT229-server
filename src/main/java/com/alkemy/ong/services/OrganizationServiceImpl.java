package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.mappers.OrganizationMapper;
import com.alkemy.ong.repositories.OrganizationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService{

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private OrganizationsRepository organizationsRepository;

    @Autowired
    private CloudStorageService amazonService;

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
    public OrganizationDTO updateOrganization(MultipartFile image, OrganizationDTO organizationDTO) throws IOException {
        Organization organization = organizationsRepository.findById(organizationDTO.getId()).get();

        if(!image.isEmpty()){
            organizationDTO.setImage(amazonService.uploadFile(image));
        }

        organization = updateInfo(organization, organizationDTO);

        organizationsRepository.save(organization);

        return organizationMapper.organizationEntity2OrganizationDTO(organization);
    }

    private Organization updateInfo(Organization organization, OrganizationDTO organizationDTO){

        if(!organizationDTO.getName().trim().isEmpty() && !organization.getName().equalsIgnoreCase(organizationDTO.getName())){
            organization.setName(organizationDTO.getName().trim());
        }
        if(organizationDTO.getImage() != null && !organization.getImage().equalsIgnoreCase(organizationDTO.getImage())){
            organization.setImage(organizationDTO.getImage().trim());
        }
        if(organizationDTO.getPhone() != 0 && organization.getPhone() != organizationDTO.getPhone()){
            organization.setPhone(organizationDTO.getPhone());
        }
        if(!organizationDTO.getAddress().trim().isEmpty() && !organization.getAddress().equalsIgnoreCase(organizationDTO.getAddress())){
            organization.setAddress(organizationDTO.getAddress().trim());
        }
        if(!organizationDTO.getEmail().trim().isEmpty() && !organization.getEmail().equalsIgnoreCase(organizationDTO.getEmail())){
            organization.setEmail(organizationDTO.getEmail().trim());
        }
        if(!organizationDTO.getWelcomeText().trim().isEmpty() && !organization.getWelcomeText().equalsIgnoreCase(organizationDTO.getWelcomeText())){
            organization.setWelcomeText(organizationDTO.getWelcomeText().trim());
        }
        if(!organizationDTO.getAboutUsText().trim().isEmpty() && !organization.getAboutUsText().equalsIgnoreCase(organizationDTO.getAboutUsText())){
            organization.setAboutUsText(organizationDTO.getAboutUsText().trim());
        }

        return organization;
    }
}
