package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.entities.ActivityEntity;
import com.alkemy.ong.mappers.ActivityMapper;
import com.alkemy.ong.repositories.ActivityRepository;
import com.alkemy.ong.services.ActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ActivitiesServiceImpl implements ActivitiesService {


    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRepository activityRepository;


    public ActivityDTO save (ActivityDTO dto) {
        Boolean entityFound = activityRepository.existsByName(dto.getName());
        if (entityFound) {
            throw new RuntimeException("Activity with the provided name is already present over the system");
        }
        ActivityEntity entity = activityMapper.activityDTO2Entity(dto);
        ActivityEntity entitySaved = activityRepository.save(entity);

        ActivityDTO dtoReturn = activityMapper.activityEntity2DTO(entitySaved);

        return dtoReturn;
    }
}
