package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.entities.ActivityEntity;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.mappers.ActivityMapper;
import com.alkemy.ong.repositories.ActivityRepository;
import com.alkemy.ong.services.ActivitiesService;
import com.alkemy.ong.services.CloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class ActivitiesServiceImpl implements ActivitiesService {

    @Autowired
    private CloudStorageService amazonS3Service;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRepository activityRepository;


    public ActivityDTO save (MultipartFile file, ActivityDTO dto) throws IOException, AmazonS3Exception, ActivityNamePresentException {
        Boolean entityFound = activityRepository.existsByName(dto.getName());
        if (entityFound) {
            throw new ActivityNamePresentException("Activity with the provided name is already present over the system");
        }

             if (file != null && !file.isEmpty()) {
                 dto.setImage(amazonS3Service.uploadFile(file));
             } else {
                 dto.setImage(null);
             }

        ActivityEntity entity = activityMapper.activityDTO2Entity(dto);
        ActivityEntity entitySaved = activityRepository.save(entity);

        ActivityDTO dtoReturn = activityMapper.activityEntity2DTO(entitySaved);

        return dtoReturn;
    }
}
