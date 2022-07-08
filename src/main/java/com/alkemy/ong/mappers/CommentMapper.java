package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.entities.CommentEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

  public CommentDTO commentEntity2dto(CommentEntity entity){
    CommentDTO dto = new CommentDTO();
    dto.setBody(entity.getBody());
    dto.setNewsId(entity.getNewsId());
    dto.setUserId(entity.getUserId());
    dto.setId(entity.getId());
    return dto;
  }

  public CommentEntity commentDto2Entity(CommentDTO dto){
    CommentEntity entity = new CommentEntity();
    entity.setBody(dto.getBody());
    entity.setNewsId(dto.getNewsId());
    entity.setUserId(dto.getUserId());
    return entity;
  }

}
