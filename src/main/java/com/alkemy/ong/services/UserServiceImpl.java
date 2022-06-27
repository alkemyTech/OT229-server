
package com.alkemy.ong.services;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.mappers.UserMapper;
import com.alkemy.ong.repositories.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserMapper mapper;

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

    @Override
    public List<UserDTO> getAll() {

        List<UserDTO> usersDTO = new LinkedList<>();
        for(User user : userRepo.findAll()){
            usersDTO.add(mapper.userEntity2DTO(user));
        }
        usersDTO.sort(Comparator.comparing(UserDTO::getEmail).reversed());
        return usersDTO;

    }
}
