package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.entities.SlidesEntity;
import org.springframework.stereotype.Component;

@Component
public class SlidesEntityMapper {

    public SlidesEntity dtoToEntity(SlidesEntityDTO dto) {
        SlidesEntity entity = new SlidesEntity();
        entity.setId(dto.getId());
        entity.setOrganizationId(dto.getOrganizationId());
        entity.setImageUrl(dto.getImageUrl());
        entity.setText(dto.getText());
        entity.setSlideOrder(dto.getSlideOrder());
        return entity;
    }

    public SlidesEntityDTO entityToDto(SlidesEntity entity) {
        return new SlidesEntityDTO()
                .setId(entity.getId())
                .setOrganizationId(entity.getOrganizationId())
                .setImageUrl(entity.getImageUrl())
                .setText(entity.getText())
                .setSlideOrder(entity.getSlideOrder());
    }

    public ReducedSlideDTO entityToReducedDTO(SlidesEntity slideEntity){
        return new ReducedSlideDTO()
                .setImageUrl(slideEntity.getImageUrl())
                .setSlideOrder(slideEntity.getSlideOrder());
    }

    public void UpdateSlide(SlidesEntity slidesEntity,SlidesEntityDTO slidesEntityDTO){
        slidesEntity.setId(slidesEntityDTO.getId());
        slidesEntity.setSlideOrder(slidesEntityDTO.getSlideOrder());
        slidesEntity.setText(slidesEntityDTO.getText());
        slidesEntity.setImageUrl(slidesEntityDTO.getImageUrl());
        slidesEntity.setOrganizationId(slidesEntityDTO.getOrganizationId()
        );
    }

}
