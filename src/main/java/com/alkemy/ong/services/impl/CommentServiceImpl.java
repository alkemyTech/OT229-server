package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.entities.CommentEntity;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.mappers.CommentMapper;
import com.alkemy.ong.repositories.CommentRepository;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserServiceImpl userService;

    @Override
    public CommentDTO save(CommentDTO commentDTO) throws Exception {
        return null;
    }

    @Override
    public List<CommentDTO> commentList(String idPost) {
        // Busco los comentarios ordenados del más viejo al más nuevo
        List<CommentEntity> commentsFound = commentRepository.findAllByNewsIdOrderByCreateDateAsc(idPost);

        if(commentsFound.isEmpty()){
            throw new EntityNotFoundException("Post with the provided ID not present");
        }

        List<CommentDTO> commentsDTOList = commentsFound.stream()
                .map(this.commentMapper::entity2DTO)
                .collect(Collectors.toList());

        return commentsDTOList;
    }

    @Override
    public CommentDTO updateComment(String idComentary, String newCommentBody, String token) throws Exception {
        Optional<CommentEntity> commentFound = commentRepository.findById(idComentary);

        if(commentFound.isPresent()){
            List<String> roles = jwtService.getRoles(token);

            String userName = jwtService.getUsername(token); // El username del token es el correo
            User user = userService.getUserByEmail(userName).get();

            if(checkPermissions(roles, commentFound.get().getId(), user.getId())){
                commentFound.get().setBody(newCommentBody);

                commentRepository.save(commentFound.get());
                return commentMapper.entity2DTO(commentFound.get());
            }else{
                throw new Exception("You don't have permissions to edit this comment");
            }
        }else{
            throw new EntityNotFoundException("Comment with the provided ID not present");
        }
    }

    @Override
    public String deleteComment(String idComentary, String token) throws Exception {
        Optional<CommentEntity> commentFound = commentRepository.findById(idComentary);

        if(commentFound.isPresent()){
            List<String> roles = jwtService.getRoles(token);

            String userName = jwtService.getUsername(token); // El username del token es el correo
            User user = userService.getUserByEmail(userName).get();

            if(checkPermissions(roles, commentFound.get().getId(), user.getId())){
                commentRepository.deleteById(commentFound.get().getId());
                return "Successfully deleted comment";

            }else{
                throw new Exception("You don't have permissions to delete this comment");
            }
        }else{
            throw new EntityNotFoundException("Comment with the provided ID not present");
        }
    }

    private boolean checkPermissions(List<String> roles, String idComment, String idUser){
        return roles.contains("ROLE_ADMIN") || idComment.equals(idUser) ? true : false;
    }
}
