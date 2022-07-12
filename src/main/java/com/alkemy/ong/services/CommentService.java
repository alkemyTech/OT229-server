package com.alkemy.ong.services;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.dto.CommentDTOList;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public interface CommentService {

    /**
     * Saves a new Comment
     *
     * @param commentDTO    the comment to be saved.
     * @return  the comment after saving.
     * @throws EntityNotFoundException  if the provided ids for the user or news article doesn't match any stored entity.
     * @throws IllegalArgumentException if the provided user id isn't a match for the currently authenticated user.
     */
    CommentDTO save (CommentDTO commentDTO) throws EntityNotFoundException, IllegalArgumentException;

    List<CommentDTO> commentList(String idPost) throws Exception;

    CommentDTO updateComment(String idComentary, String newCommentBody) throws Exception;
    
    String deleteComment(String idComentary) throws Exception;

    List<CommentDTOList>getAll();
}
