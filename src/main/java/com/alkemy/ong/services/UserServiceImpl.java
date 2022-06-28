
package com.alkemy.ong.services;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.mappers.UserMapper;
import com.alkemy.ong.repositories.UserRepository;
import com.alkemy.ong.security.service.JwtService;
import com.amazonaws.services.apigateway.model.Op;
import javassist.NotFoundException;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepo;

    @Autowired
    JwtService jwtService;
    @Autowired
    UserMapper mapper;
    public UserDTO getMe(String jwt) throws Exception{
        String emailUser = jwtService.getUsername(jwt);
        Boolean exitsUser = userRepo.existsByEmail(emailUser);
        if (!exitsUser) throw new NotFoundException("A user with this token was not found");
        Optional<User> user = userRepo.findByEmail(emailUser);
        return mapper.userEntity2DTO(user.get());

    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return this.userRepo.findByEmail(email);
    }

    @Override
    public String delete(String id) throws NotFoundException {

        boolean userExistis = userRepo.existsById(id);
        if (!userExistis) throw new NotFoundException("A user with id " + id + " was not found");

        userRepo.deleteById(id);

        return "Successfully deleted user with id " + id;
    }

}
