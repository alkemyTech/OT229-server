package com.alkemy.ong.services;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import javassist.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;

public interface SlidesService {

    List<SlidesEntityDTO> findByOrganization(String organizationId);

    SlidesEntityDTO findById(String slideId) throws RuntimeException;

    List<ReducedSlideDTO> slideList();

    SlidesEntityDTO create(MultipartFile file,SlidesEntityDTO slide) throws IOException;

    SlidesEntityDTO deleteSlide(String id) throws NotFoundException, IOException;

    SlidesEntityDTO updateSlide(String id,MultipartFile file,SlidesEntityDTO slide)throws EntityNotFoundException, IOException, AmazonS3Exception, IllegalArgumentException;
}
