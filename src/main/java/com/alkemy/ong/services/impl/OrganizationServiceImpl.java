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

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {

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
            if(!image.isEmpty()){
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

    private Organization updateInfo(Organization organization, OrganizationDTO organizationDTO){

        if(organizationDTO.getName() != null && !organizationDTO.getName().trim().isEmpty() && !organization.getName().equalsIgnoreCase(organizationDTO.getName())){
            organization.setName(organizationDTO.getName().trim());
        }
        if(organizationDTO.getImage() != null && !organization.getImage().equalsIgnoreCase(organizationDTO.getImage())){
            organization.setImage(organizationDTO.getImage().trim());
        }
        if(organizationDTO.getPhone() != 0 && organization.getPhone() != organizationDTO.getPhone()){
            organization.setPhone(organizationDTO.getPhone());
        }
        if(organizationDTO.getAddress() != null && !organizationDTO.getAddress().trim().isEmpty() && !organization.getAddress().equalsIgnoreCase(organizationDTO.getAddress())){
            organization.setAddress(organizationDTO.getAddress().trim());
        }
        if(organizationDTO.getEmail() != null && !organizationDTO.getEmail().trim().isEmpty() && !organization.getEmail().equalsIgnoreCase(organizationDTO.getEmail())){
            organization.setEmail(organizationDTO.getEmail().trim());
        }
        if(organizationDTO.getWelcomeText() != null && !organizationDTO.getWelcomeText().trim().isEmpty() && !organization.getWelcomeText().equalsIgnoreCase(organizationDTO.getWelcomeText())){
            organization.setWelcomeText(organizationDTO.getWelcomeText().trim());
        }
        if(organizationDTO.getAboutUsText() != null && !organizationDTO.getAboutUsText().trim().isEmpty() && !organization.getAboutUsText().equalsIgnoreCase(organizationDTO.getAboutUsText())){
            organization.setAboutUsText(organizationDTO.getAboutUsText().trim());
        }
        if (organizationDTO.getUrlFacebook() != null && !organizationDTO.getUrlFacebook().trim().isEmpty() && !organization.getUrlFacebook().equalsIgnoreCase(organizationDTO.getUrlFacebook())){
            organization.setUrlFacebook(organizationDTO.getUrlFacebook().trim());
        }
        if (organizationDTO.getUrlInstagram() != null && !organizationDTO.getUrlInstagram().trim().isEmpty() && !organization.getUrlInstagram().equalsIgnoreCase(organizationDTO.getUrlInstagram())){
            organization.setUrlInstagram(organizationDTO.getUrlInstagram().trim());
        }
        if (organizationDTO.getUrlLinkedin() != null && !organizationDTO.getUrlLinkedin().trim().isEmpty() && !organization.getUrlLinkedin().equalsIgnoreCase(organizationDTO.getUrlLinkedin())){
            organization.setUrlLinkedin(organizationDTO.getUrlLinkedin().trim());
        }
        return organization;

    }

}
