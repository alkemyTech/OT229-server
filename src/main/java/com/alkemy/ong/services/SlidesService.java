package com.alkemy.ong.services;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface SlidesService {

    List<SlidesEntityDTO> findByOrganization(String organizationId);

    SlidesEntityDTO findById(String slideId) throws RuntimeException;

    List<ReducedSlideDTO> slideList();

    SlidesEntityDTO create(MultipartFile file,SlidesEntityDTO slide) throws CloudStorageClientException, CorruptedFileException;

    SlidesEntityDTO deleteSlide(String id) throws EntityNotFoundException, CloudStorageClientException, FileNotFoundOnCloudException;
}
