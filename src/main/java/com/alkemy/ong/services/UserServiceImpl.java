
package com.alkemy.ong.services;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepo;

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return this.userRepo.findByEmail(email);
    }


}
