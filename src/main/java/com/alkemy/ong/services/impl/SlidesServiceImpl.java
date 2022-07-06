package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.mappers.SlidesEntityMapper;
import com.alkemy.ong.repositories.SlideRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.SlidesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
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
    public SlidesEntityDTO create(MultipartFile file, SlidesEntityDTO slide) throws CloudStorageClientException, CorruptedFileException {
        SlidesEntity entity=this.slidesMapper.dtoToEntity(slide);

        if (entity.getSlideOrder()==null) {
            entity.setSlideOrder(slideRepository.getLastOrder(entity.getOrganizationId())+1);
        }

        String imageUrl= cloudStorageService.uploadFile(file);
        slide.setImageUrl(imageUrl);

        SlidesEntity entitySaved=this.slideRepository.save(entity);


        return this.slidesMapper.entityToDto(entitySaved);
    }

    @Override
    public SlidesEntityDTO deleteSlide(String id) throws EntityNotFoundException, CloudStorageClientException, FileNotFoundOnCloudException {
        SlidesEntity slide = slideRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Slide with the provided id not found."));
        SlidesEntityDTO dto = this.slidesMapper.entityToDto(slide);
        this.slideRepository.delete(slide);
        cloudStorageService.deleteFileFromS3Bucket(slide.getImageUrl());
        return dto;
    }

    @Override
    public SlidesEntityDTO updateSlide(String id, MultipartFile file, SlidesEntityDTO slide) throws EntityNotFoundException, IllegalArgumentException, CloudStorageClientException, CorruptedFileException {
        SlidesEntity entity = this.slideRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Slide whit the provide id not found"));
        this.slidesMapper.UpdateSlide(entity,slide);
        String newImage = slide.getImageUrl();
        if (file != null && !file.isEmpty()){
            newImage=cloudStorageService.uploadFile(file);
        }
        slide.setImageUrl(newImage);
        return this.slidesMapper.entityToDto(entity);
    }


}
