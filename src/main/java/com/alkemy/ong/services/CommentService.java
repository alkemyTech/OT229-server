package com.alkemy.ong.services;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.dto.CommentDTOList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    public CommentDTO save (CommentDTO commentDTO) throws Exception;

    List<CommentDTO> commentList(String idPost) throws Exception;

    CommentDTO updateComment(String idComentary, String token, String newCommentBody) throws Exception;
    
    String deleteComment(String idComentary, String token) throws Exception;

    List<CommentDTOList>getAll();
}
