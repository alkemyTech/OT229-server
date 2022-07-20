package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.OrganizationDTORequest;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.mappers.OrganizationMapper;
import com.alkemy.ong.repositories.OrganizationsRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;
    private final OrganizationsRepository organizationsRepository;
    private final CloudStorageService amazonService;

    @Autowired
    public OrganizationServiceImpl(OrganizationMapper organizationMapper, OrganizationsRepository organizationsRepository, CloudStorageService amazonService) {
        this.organizationMapper = organizationMapper;
        this.organizationsRepository = organizationsRepository;
        this.amazonService = amazonService;
    }

    @Override
    public List<ReducedOrganizationDTO> getAll() {
        List<Organization> entities = organizationsRepository.findAll();
        List<ReducedOrganizationDTO> dtos = organizationMapper.organizationEntity2ReducedDTOList(entities);

        return dtos;
    }

    @Override
    public ReducedOrganizationDTO getById(String id) throws RuntimeException {
        Optional<Organization> entity = organizationsRepository.findById(id);
        if (!entity.isPresent()) {
            throw new RuntimeException("Organization with the provided ID not present");
        }
        ReducedOrganizationDTO dto = organizationMapper.organizationEntity2ReducedDTO(entity.get());
        return dto;
    }

    @Override
    public OrganizationDTO updateOrganization(MultipartFile image, OrganizationDTO organizationDTO) throws RuntimeException, CloudStorageClientException, CorruptedFileException {
        Optional<Organization> organizationFound = organizationsRepository.findById(organizationDTO.getId());

        if(organizationFound.isPresent()){
            if(image != null && !image.isEmpty()){
                try{
                    organizationDTO.setImage(amazonService.uploadFile(image));
                }catch(CloudStorageClientException e){
                    throw new CloudStorageClientException("Image could not be saved. Try again later.");
                }
            }

            Organization organizationUpdated = updateInfo(organizationFound.get(), organizationDTO);

            organizationsRepository.save(organizationUpdated);

            return organizationMapper.organizationEntity2OrganizationDTO(organizationUpdated);
        }else{
            throw new RuntimeException("Organization with the provided ID not present");
        }
    }

    @Override
    public OrganizationDTO updateOrganization(OrganizationDTORequest organizationDTO) throws RuntimeException, CloudStorageClientException, CorruptedFileException {
        Optional<Organization> organizationFound = organizationsRepository.findById(organizationDTO.getId());

        if(organizationFound.isPresent()){
            if(organizationDTO.getEncoded_image() != null){
                try{
                    organizationDTO.setImage(amazonService.uploadBase64File(
                            organizationDTO.getEncoded_image().getEncoded_string(),
                            organizationDTO.getEncoded_image().getFile_name()
                    ));
                }catch(CloudStorageClientException e){
                    throw new CloudStorageClientException("Image could not be saved. Try again later.");
                }
            }

            Organization organizationUpdated = updateInfo(organizationFound.get(), organizationDTO);

            organizationsRepository.save(organizationUpdated);

            return organizationMapper.organizationEntity2OrganizationDTO(organizationUpdated);
        }else{
            throw new RuntimeException("Organization with the provided ID not present");
        }
    }

    private Organization updateInfo(Organization organization, @Valid OrganizationDTO organizationDTO){
        organization.setName(organizationDTO.getName());
        organization.setImage(organizationDTO.getImage());
        organization.setEmail(organizationDTO.getEmail());
        organization.setWelcomeText(organizationDTO.getWelcomeText());
        organization.setPhone(organizationDTO.getPhone());
        organization.setAddress(organizationDTO.getAddress());
        organization.setAboutUsText(organizationDTO.getAboutUsText());
        organization.setUrlFacebook(organizationDTO.getUrlFacebook());
        organization.setUrlInstagram(organizationDTO.getUrlInstagram());
        organization.setUrlLinkedin(organizationDTO.getUrlLinkedin());
        return organization;
    }

}
