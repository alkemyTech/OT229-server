package com.alkemy.ong.services;

import com.alkemy.ong.dto.CommentDTO;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
  public CommentDTO save (CommentDTO commentDTO) throws Exception;

    void deleteComment(String idComentary, String token) throws Exception;
}
