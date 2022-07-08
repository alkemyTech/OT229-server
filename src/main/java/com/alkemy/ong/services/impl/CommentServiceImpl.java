package com.alkemy.ong.services.impl;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserServiceImpl userService;

    @Override
    public String deleteComment(String idComentary, String token) throws Exception {
        try{
            // Buscar el comentario por ID, buscar los roles del usuario
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Comment with the provided ID not present");
        }
        // Extraigo los roles
        List<String> roles = jwtService.getRoles(token);

        // Extraigo el usarname para saber si es propietario del comentario
        String userName = jwtService.getUsername(token); // El username del token es el correo
        User user = userService.getUserByEmail(userName).get();

        // Si es administrador lo puede eliminar
        if(roles.contains("ROLE_ADMIN")){
            // Lo elimina
            return "Successfully deleted comment";
        }else if(true){ // Verifico si es el propietario del comentario
            // comment.getUserId().equals(user.getId()) -> delete
            return "Successfully deleted comment";
        }else{
            throw new Exception("You don't have permissions to delete this comment");
        }
    }
}
