package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.DatedNewsDTO;
import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.entities.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {
  @Autowired
  private CategoryMapper categoryMapper;

  public News newsDTO2Entity(NewsDTO dto){
    News newsEntity = new News();
    newsEntity.setId(dto.getId());
    newsEntity.setName(dto.getName());
    newsEntity.setContent(dto.getContent());
    newsEntity.setImage(dto.getImage());
    newsEntity.setCategory(this.categoryMapper.categoryDTO2Entity(dto.getCategory()));
    return newsEntity;
  }

  public NewsDTO newsEntity2DTO(News entity){
    NewsDTO dto = new NewsDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setContent(entity.getContent());
    dto.setImage(entity.getImage());
    dto.setCategory(this.categoryMapper.categoryEntity2DTO(entity.getCategory()));
    return dto;
  }

  public void UpdateNewsInstance(News newsToBeUpdated, NewsDTO updatedNews) {
    newsToBeUpdated.setId(updatedNews.getId());
    newsToBeUpdated.setName(updatedNews.getName());
    newsToBeUpdated.setContent(updatedNews.getContent());
    newsToBeUpdated.setImage(updatedNews.getImage());
    newsToBeUpdated.setCategory(this.categoryMapper.categoryDTO2Entity(updatedNews.getCategory()));
  }

  public DatedNewsDTO newsEntity2DatedDTO(News entity) {
    DatedNewsDTO dto = new DatedNewsDTO();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setContent(entity.getContent());
    dto.setImage(entity.getImage());
    dto.setCategory(this.categoryMapper.categoryEntity2DTO(entity.getCategory()));
    dto.setTimestamp( entity.getTimestamp().toString() );
    return dto;
  }

}
