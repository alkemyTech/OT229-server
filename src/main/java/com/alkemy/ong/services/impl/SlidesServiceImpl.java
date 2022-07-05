package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import com.alkemy.ong.mappers.SlidesEntityMapper;
import com.alkemy.ong.repositories.SlideRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.SlidesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlidesServiceImpl implements SlidesService {

    @Autowired
    private SlideRepository slideRepository;
    @Autowired
    private SlidesEntityMapper slidesMapper;
    @Autowired
    private CloudStorageService cloudStorageService;
    @Override
    public List<SlidesEntityDTO> findByOrganization(String organizationId) {
        return this.slideRepository.findByOrganizationIdOrderBySlideOrder(organizationId)
                .stream()
                .map(this.slidesMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SlidesEntityDTO findById(String slideId) throws RuntimeException {
        Optional<SlidesEntity> slideFound = slideRepository.findById(slideId);

        if(slideFound.isPresent()){
            return slidesMapper.entityToDto(slideFound.get());
        }else{
            throw new RuntimeException("Slide with the provided ID not present");
        }
    }

    @Override
    public List<ReducedSlideDTO> slideList(){
        List<ReducedSlideDTO> slidesFound = slideRepository.findAllByOrderBySlideOrderAsc()
                .stream()
                .map(this.slidesMapper::entityToReducedDTO)
                .collect(Collectors.toList());

        return slidesFound;
    }

    @Override
    public SlidesEntityDTO create(MultipartFile file, SlidesEntityDTO slide) throws IOException {
        SlidesEntity entity=this.slidesMapper.dtoToEntity(slide);

        if (entity.getSlideOrder()==null) {
            entity.setSlideOrder(slideRepository.getLastOrder(entity.getOrganizationId())+1);
        }

        String imageUrl= cloudStorageService.uploadFile(file);
        slide.setImageUrl(imageUrl);

        SlidesEntity entitySaved=this.slideRepository.save(entity);


        return this.slidesMapper.entityToDto(entitySaved);
    }

}
