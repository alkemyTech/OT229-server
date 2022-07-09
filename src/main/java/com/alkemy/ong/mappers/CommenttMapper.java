package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.entities.CommentEntity;
import org.springframework.stereotype.Component;

import javax.persistence.Column;

@Component
public class CommenttMapper {

    public CommentDTO entity2DTO(CommentEntity entity){
        return new CommentDTO().setId(entity.getId())
                .setNewsId(entity.getNewsId())
                .setUserId(entity.getUserId())
                .setBody(entity.getBody());
    }
}
