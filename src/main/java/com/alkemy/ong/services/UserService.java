
package com.alkemy.ong.services;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.entities.User;
import javassist.NotFoundException;
import com.alkemy.ong.exception.AmazonS3Exception;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.Optional;


public interface UserService {


  public User save(User user);
  Optional<User> getUserByEmail(String email);
  String delete(String id) throws NotFoundException;
  UserDTO updateUser(MultipartFile file, UserDTORequest userDTOrequest) throws NotFoundException, IOException, AmazonS3Exception;
  List<UserDTO> getAll();

}
