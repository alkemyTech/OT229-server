
package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.mappers.UserMapper;
import com.alkemy.ong.repositories.UserRepository;
import com.alkemy.ong.security.payload.SignupRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.EmailService;
import com.alkemy.ong.services.RoleService;
import com.alkemy.ong.services.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserMapper mapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CloudStorageService amazonS3Service;

    @Autowired
    JwtService jwtService;

    @Autowired
    EmailService emailService;

    @Autowired
    RoleService roleService;

    @Override
    public SingupResponse createUser(SignupRequest signupRequest, MultipartFile image) throws IOException {
        User user = new User();

        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        if(image != null && !image.isEmpty()){
            user.setPhoto(amazonS3Service.uploadFile(image));
        }

        user.setRoleId(roleService.getRoleUser());

        save(user);
        emailService.sendEmail(user.getEmail());

        SingupResponse singupResponse = new SingupResponse();
        singupResponse.setUser(mapper.userEntity2DTO(user));
        singupResponse.setMessage("Successful registration");
        singupResponse.setToken(jwtService.createToken(user));

        return singupResponse;
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

    @Override
    public UserDTO updateUser(MultipartFile file, UserDTORequest userDTOrequest) throws NotFoundException, IOException, AmazonS3Exception {
        Boolean exists = userRepo.existsById(userDTOrequest.getId());
        if (!exists) throw new NotFoundException("A user with id " + userDTOrequest.getId() + " was not found");
        User user = userRepo.getById(userDTOrequest.getId());

        if (!userDTOrequest.getEmail().isEmpty()) user.setEmail(userDTOrequest.getEmail());
        if (!userDTOrequest.getFirstName().isEmpty()) user.setFirstName(userDTOrequest.getFirstName());
        if (!userDTOrequest.getLastName().isEmpty()) user.setLastName(userDTOrequest.getLastName());
        if (!userDTOrequest.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userDTOrequest.getPassword()));
        if (!file.isEmpty()) user.setPhoto(amazonS3Service.uploadFile(file));

        userRepo.save(user);

        return mapper.userEntity2DTO(user);
    }

    public List<UserDTO> getAll() {

        List<UserDTO> usersDTO = new LinkedList<>();
        for(User user : userRepo.findAll()){
            usersDTO.add(mapper.userEntity2DTO(user));
        }
        usersDTO.sort(Comparator.comparing(UserDTO::getEmail).reversed());
        return usersDTO;

    }

    public UserDTO getMe(String jwt) throws Exception{
        String emailUser = jwtService.getUsername(jwt);
        Boolean exitsUser = userRepo.existsByEmail(emailUser);
        if (!exitsUser) throw new NotFoundException("A user with this token was not found");
        Optional<User> user = userRepo.findByEmail(emailUser);
        return mapper.userEntity2DTO(user.get());

    }

}
