package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.dto.CommentDTOList;
import com.alkemy.ong.entities.CommentEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDTO entity2DTO(CommentEntity entity){
        return new CommentDTO().setId(entity.getId())
                .setPost_id(entity.getNewsId())
                .setUser_id(entity.getUserId())
                .setBody(entity.getBody());
    }

    public CommentDTOList entity2DTOList(CommentEntity entity){
        return new CommentDTOList().setBody(entity.getBody());
    }

    public CommentEntity dto2Entity(CommentDTO dto){
        CommentEntity entity = new CommentEntity();
        entity.setBody(dto.getBody());
        entity.setNewsId(dto.getPost_id());
        entity.setUserId(dto.getUser_id());
        return entity;
    }

}
