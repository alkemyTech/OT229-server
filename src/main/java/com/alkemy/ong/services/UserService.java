
package com.alkemy.ong.services;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.User;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.Optional;


public interface UserService {


  public User save(User user);
  Optional<User> getUserByEmail(String email);
  String delete(String id) throws NotFoundException;

  UserDTO getMe(String jwt) throws Exception;

}
