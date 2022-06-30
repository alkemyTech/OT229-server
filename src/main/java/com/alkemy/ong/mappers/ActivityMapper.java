package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.entities.ActivityEntity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityEntity activityDTO2Entity (ActivityDTO dto) {
        ActivityEntity entity = new ActivityEntity();

        entity.setImage(dto.getImage());
        entity.setName(dto.getName());
        entity.setContent(dto.getContent());

        return entity;
    }

    public ActivityDTO activityEntity2DTO (ActivityEntity entity) {
        ActivityDTO dto = new ActivityDTO();

        dto.setImage(entity.getImage());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());

        return dto;
    }
}
