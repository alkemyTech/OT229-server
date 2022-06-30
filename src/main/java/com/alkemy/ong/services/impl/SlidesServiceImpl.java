package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.mappers.SlidesEntityMapper;
import com.alkemy.ong.repositories.SlideRepository;
import com.alkemy.ong.services.SlidesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

}
