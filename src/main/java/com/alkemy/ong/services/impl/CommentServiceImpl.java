package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.entities.CommentEntity;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.repositories.CommentRepository;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserServiceImpl userService;

    @Override
    public CommentDTO save(CommentDTO commentDTO) throws Exception {
        return null;
    }

    @Override
    public String deleteComment(String idComentary, String token) throws Exception {
        Optional<CommentEntity> commentFound = commentRepository.findById(idComentary);

        if(commentFound.isPresent()){
            List<String> roles = jwtService.getRoles(token);

            String userName = jwtService.getUsername(token); // El username del token es el correo
            User user = userService.getUserByEmail(userName).get();

            // Si es administrador lo puede eliminar
            if(roles.contains("ROLE_ADMIN")){
                commentRepository.deleteById(commentFound.get().getId());
                return "Successfully deleted comment";

            }else if(commentFound.get().getUserId().equals(user.getId())){ // Verifico si es el propietario del comentario
                commentRepository.deleteById(commentFound.get().getId());
                return "Successfully deleted comment";

            }else{
                throw new Exception("You don't have permissions to delete this comment");
            }
        }else{
            throw new EntityNotFoundException("Comment with the provided ID not present");
        }
    }
}
