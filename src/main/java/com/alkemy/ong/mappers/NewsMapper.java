package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.entities.News;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {

    public News newsDTO2Entity(NewsDTO dto){
        News newsEntity = new News();
        newsEntity.setId(dto.getId());
        newsEntity.setName(dto.getName());
        newsEntity.setContent(dto.getContent());
        newsEntity.setImage(dto.getImage());
        return newsEntity;
    }

    public NewsDTO newsEntity2DTO(News entity){
        NewsDTO dto = new NewsDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setImage(entity.getImage());
        return dto;
    }
}
