package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.entities.CommentEntity;
import com.alkemy.ong.mappers.CommentMapper;
import com.alkemy.ong.repositories.CommentRepository;
import com.alkemy.ong.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private CommentRepository commentRepository;

  @Override
  public CommentDTO save(CommentDTO commentDTO)throws Exception {
    CommentEntity newComment = commentMapper.commentDto2Entity(commentDTO);
    CommentEntity commentSaved = commentRepository.save(newComment);
    CommentDTO result = commentMapper.commentEntity2dto(commentSaved);
    return result;
  }
}
