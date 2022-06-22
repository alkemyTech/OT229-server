
package com.alkemy.ong.services;

import com.alkemy.ong.entities.User;

import java.util.Optional;


public interface UserService {
    
    
  public User save(User user);
  Optional<User> getUserByEmail(String email);
   
}
