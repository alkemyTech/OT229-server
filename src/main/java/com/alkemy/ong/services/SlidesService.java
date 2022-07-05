package com.alkemy.ong.services;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SlidesService {

    List<SlidesEntityDTO> findByOrganization(String organizationId);

    SlidesEntityDTO findById(String slideId) throws RuntimeException;

    List<ReducedSlideDTO> slideList();

    SlidesEntityDTO create(MultipartFile file,SlidesEntityDTO slide) throws IOException;
}
