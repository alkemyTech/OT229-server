package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.dto.ActivityDTORequest;
import com.alkemy.ong.entities.ActivityEntity;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.ActivityNotFoundException;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.mappers.ActivityMapper;
import com.alkemy.ong.repositories.ActivityRepository;
import com.alkemy.ong.services.ActivitiesService;
import com.alkemy.ong.services.CloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
public class ActivitiesServiceImpl implements ActivitiesService {

    private CloudStorageService amazonS3Service;

    private ActivityMapper activityMapper;

    private ActivityRepository activityRepository;

    @Autowired
    public ActivitiesServiceImpl(ActivityMapper activityMapper, ActivityRepository activityRepository, CloudStorageService amazonS3Service){
        this.activityMapper = activityMapper;
        this.activityRepository = activityRepository;
        this.amazonS3Service = amazonS3Service;
    }

    @Override
    public ActivityDTO save (MultipartFile file, ActivityDTO dto) throws CloudStorageClientException, ActivityNamePresentException, CorruptedFileException {
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

    @Override
    public ActivityDTO save(ActivityDTORequest dto) throws CloudStorageClientException, ActivityNamePresentException, CorruptedFileException {
        Boolean entityFound = activityRepository.existsByName(dto.getName());
        if (entityFound) {
            throw new ActivityNamePresentException("Activity with the provided name is already present over the system");
        }

        if (dto.getEncoded_image() != null) {
            dto.setImage(amazonS3Service.uploadBase64File(
                    dto.getEncoded_image().getEncoded_string(),
                    dto.getEncoded_image().getFile_name()
            ));
        } else {
            dto.setImage(null);
        }

        ActivityEntity entity = activityMapper.activityDTO2Entity(dto);
        ActivityEntity entitySaved = activityRepository.save(entity);

        ActivityDTO dtoReturn = activityMapper.activityEntity2DTO(entitySaved);

        return dtoReturn;
    }


    @Override
    public ActivityDTO edit(MultipartFile file, ActivityDTO dto, String id) throws CloudStorageClientException, ActivityNamePresentException, ActivityNotFoundException, CorruptedFileException {
        Optional<ActivityEntity> entityFound = activityRepository.findById(id);

        if (!entityFound.isPresent()) {
            throw new ActivityNotFoundException("Activity with the provided ID was not found over the system");
        }

        Optional<ActivityEntity> entitySameName = activityRepository.findByName(dto.getName());

        if (entitySameName.isPresent() && entitySameName.get().getId() != entityFound.get().getId()) {
            throw new ActivityNamePresentException("Cannot change to the provided name as it should be unique over the system");
        }

        if (file != null && !file.isEmpty()) {
            dto.setImage(amazonS3Service.uploadFile(file));
        } else {
            dto.setImage(null);
        }

        ActivityEntity modifiedEntity = activityMapper.editEntity(entityFound.get(), dto);
        activityRepository.save(modifiedEntity);
        ActivityDTO result = activityMapper.activityEntity2DTO(modifiedEntity);

        return result;
    }

    @Override
    public ActivityDTO edit(ActivityDTORequest dto, String id) throws CloudStorageClientException, ActivityNamePresentException, ActivityNotFoundException, CorruptedFileException {
        Optional<ActivityEntity> entityFound = activityRepository.findById(id);

        if (!entityFound.isPresent()) {
            throw new ActivityNotFoundException("Activity with the provided ID was not found over the system");
        }

        Optional<ActivityEntity> entitySameName = activityRepository.findByName(dto.getName());

        if (entitySameName.isPresent() && entitySameName.get().getId() != entityFound.get().getId()) {
            throw new ActivityNamePresentException("Cannot change to the provided name as it should be unique over the system");
        }

        if (dto.getEncoded_image() != null) {
            dto.setImage(amazonS3Service.uploadBase64File(
                    dto.getEncoded_image().getEncoded_string(),
                    dto.getEncoded_image().getFile_name()
            ));
        } else {
            dto.setImage(null);
        }

        ActivityEntity modifiedEntity = activityMapper.editEntity(entityFound.get(), dto);
        activityRepository.save(modifiedEntity);
        ActivityDTO result = activityMapper.activityEntity2DTO(modifiedEntity);

        return result;
    }

}
