
package com.alkemy.ong.services;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.RegisterException;
import com.alkemy.ong.security.payload.SignupRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import javassist.NotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.Optional;


public interface UserService {
  SingupResponse createUser(SignupRequest  signupRequest, MultipartFile image) throws IOException, CloudStorageClientException, CorruptedFileException;
  SingupResponse createUser(SignupRequest  signupRequest) throws IOException, CloudStorageClientException, CorruptedFileException;

  public User save(User user);
  Optional<User> getUserByEmail(String email);
  String delete(String id) throws NotFoundException;
  UserDTO updateUser(MultipartFile file, UserDTORequest userDTOrequest) throws Exception;
  UserDTO updateUser(UserDTORequest userDTOrequest) throws Exception;
  List<UserDTO> getAll();
  UserDTO getMe(String jwt) throws Exception;

  boolean existsById(String id);

}
