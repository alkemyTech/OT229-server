
package com.alkemy.ong.services;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.repositories.UserRepository;
import javassist.NotFoundException;
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

    @Override
    public String delete(String id) throws NotFoundException {
        boolean userExistis = userRepo.existsById(id);
        if(!userExistis)throw new NotFoundException("A user with id "+id+" was not found");

        User u= userRepo.getById(id);

        if(u.isSoftDelete())throw new NotFoundException("Please enter the id of a user without deleting");
        u.setSoftDelete(true);

        return "Successfully deleted user with id "+id;
    }


}
