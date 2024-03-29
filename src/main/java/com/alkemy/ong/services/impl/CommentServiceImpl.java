package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.dto.CommentDTOList;
import com.alkemy.ong.entities.CommentEntity;
import com.alkemy.ong.entities.Role;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.mappers.CommentMapper;
import com.alkemy.ong.repositories.CommentRepository;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.services.CommentService;
import com.alkemy.ong.services.NewsService;
import com.alkemy.ong.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Autowired
    AuthenticationService authenticationService;

    @Override
    public CommentDTO save(CommentDTO commentDTO) throws EntityNotFoundException, IllegalArgumentException {
        if ( ! this.userService.existsById(commentDTO.getUser_id()) ) {
            throw new EntityNotFoundException("Associated User not found");
        }
        if ( ! this.authenticationService.authUserMatchesId(commentDTO.getUser_id()) ) {
            throw new IllegalArgumentException("The provided user id doesn't match the currently authenticated user.");
        }
        if ( ! this.newsService.existNew(commentDTO.getPost_id()) ) {
            throw new EntityNotFoundException("Associated news article not found");
        }
        CommentEntity newComment = commentMapper.dto2Entity(commentDTO);
        CommentEntity commentSaved = commentRepository.save(newComment);
        CommentDTO result = commentMapper.entity2DTO(commentSaved);
        return result;
    }

    @Override
    public List<CommentDTO> commentList(String idPost) throws Exception{
        boolean existPost = newsService.existNew(idPost);

        if(!existPost){
            throw new EntityNotFoundException("Post with the provided ID not present");
        }

        // Busco los comentarios ordenados del más viejo al más nuevo
        List<CommentEntity> commentsFound = commentRepository.findAllByNewsIdOrderByCreateDateAsc(idPost);

        List<CommentDTO> commentsDTOList = commentsFound.stream()
                .map(this.commentMapper::entity2DTO)
                .collect(Collectors.toList());

        return commentsDTOList;
    }

    @Override
    public CommentDTO updateComment(String idComentary, String newCommentBody) throws Exception {
        Optional<CommentEntity> commentFound = commentRepository.findById(idComentary);

        if(commentFound.isEmpty()){
            throw new EntityNotFoundException("Comment with the provided ID not present");
        }

        Optional<User> userAuthOptional = authenticationService.getAuthenticatedUserEntity();

        if(userAuthOptional.isEmpty()){
            throw new Exception("You don't have permissions to edit this comment");
        }

        User userAuth = userAuthOptional.get();

        if(checkPermissions(userAuth.getRoleId(), commentFound.get().getUserId(), userAuth.getId())){
            commentFound.get().setBody(newCommentBody);
            commentRepository.save(commentFound.get());
            return commentMapper.entity2DTO(commentFound.get());
        }else{
            throw new Exception("You don't have permissions to edit this comment");
        }
    }

    @Override
    public String deleteComment(String idComentary) throws Exception {
        Optional<CommentEntity> commentFound = commentRepository.findById(idComentary);

        if(commentFound.isEmpty()){
            throw new EntityNotFoundException("Comment with the provided ID not present");
        }

        Optional<User> userAuthOptional = authenticationService.getAuthenticatedUserEntity();

        if(userAuthOptional.isEmpty()){
            throw new Exception("You don't have permissions to edit this comment");
        }

        User userAuth = userAuthOptional.get();

        if(checkPermissions(userAuth.getRoleId(), commentFound.get().getUserId(), userAuth.getId())){
            commentRepository.deleteById(commentFound.get().getId());
            return "Successfully deleted comment";
        }else{
            throw new Exception("You don't have permissions to delete this comment");
        }
    }

    @Override
    public List<CommentDTOList> getAll() {

        List<CommentDTOList> commentDTOS = new LinkedList<>();
        for (CommentEntity comment : commentRepository.findAllByOrderByCreateDateDesc()) {
            commentDTOS.add(commentMapper.entity2DTOList(comment));
        }

        return commentDTOS;

    }

    private boolean checkPermissions(Set<Role> roles, String idComment, String idUser){
        boolean isAdmin = roles.stream().
                anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        return isAdmin || idComment.equals(idUser);
    }
}
