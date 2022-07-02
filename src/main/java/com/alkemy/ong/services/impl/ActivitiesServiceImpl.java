package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.entities.ActivityEntity;
import com.alkemy.ong.exception.ActivityException;
import com.alkemy.ong.exception.ActivityNotFoundException;
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
import java.util.Optional;


@Service
public class ActivitiesServiceImpl implements ActivitiesService {

    @Autowired
    private CloudStorageService amazonS3Service;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRepository activityRepository;


    public ResponseEntity<?> save (MultipartFile file, ActivityDTO dto) throws IOException {
        Boolean entityFound = activityRepository.existsByName(dto.getName());
        if (entityFound) {
            throw new ActivityException("Activity with the provided name is already present over the system");
        }

             if (file != null && !file.isEmpty()) {
                 dto.setImage(amazonS3Service.uploadFile(file));
             } else {
                 dto.setImage(null);
             }

        ActivityEntity entity = activityMapper.activityDTO2Entity(dto);
        ActivityEntity entitySaved = activityRepository.save(entity);

        ActivityDTO dtoReturn = activityMapper.activityEntity2DTO(entitySaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoReturn);
    }

    @Override
    public ResponseEntity<?> edit(MultipartFile file, ActivityDTO dto, String id) throws IOException {
        Optional<ActivityEntity> entityFound = activityRepository.findById(id);

        if (!entityFound.isPresent()) {
            throw new ActivityNotFoundException("Activity with the provided ID was not found over the system");
        }

        Optional<ActivityEntity> entitySameName = activityRepository.findByName(dto.getName());

        if (entitySameName.isPresent() && entitySameName.get().getId() != entityFound.get().getId()) {
            throw new ActivityException("Cannot change to the provided name as it should be unique over the system");
        }

        if (file != null && !file.isEmpty()) {
            dto.setImage(amazonS3Service.uploadFile(file));
        } else {
            dto.setImage(null);
        }

        ActivityEntity modifiedEntity = activityMapper.editEntity(entityFound.get(), dto);
        activityRepository.save(modifiedEntity);
        ActivityDTO result = activityMapper.activityEntity2DTO(modifiedEntity);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
