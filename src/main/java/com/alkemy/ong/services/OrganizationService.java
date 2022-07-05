package com.alkemy.ong.services;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationService {

    public List<ReducedOrganizationDTO> getAll();

    /**
     * Returns organization's most relevant fields.
     *
     * @param id the id of the organization.
     * @return  the dto of the organization.
     * @throws RuntimeException if the organization with the received id doesn't exist.
     */
    public ReducedOrganizationDTO getById (String id) throws RuntimeException;

    public OrganizationDTO updateOrganization(MultipartFile image, OrganizationDTO organizationDTO) throws RuntimeException, CloudStorageClientException, CorruptedFileException;
}
