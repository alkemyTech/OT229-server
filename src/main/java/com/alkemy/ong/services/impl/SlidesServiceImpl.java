package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import com.alkemy.ong.mappers.SlidesEntityMapper;
import com.alkemy.ong.repositories.SlideRepository;
import com.alkemy.ong.services.SlidesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlidesServiceImpl implements SlidesService {

    @Autowired
    private SlideRepository slideRepository;
    @Autowired
    private SlidesEntityMapper slidesMapper;

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

}
