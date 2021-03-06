
package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.RegisterException;
import com.alkemy.ong.mappers.UserMapper;
import com.alkemy.ong.repositories.UserRepository;
import com.alkemy.ong.security.payload.SignupRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.EmailService;
import com.alkemy.ong.services.RoleService;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepo;


    private UserMapper mapper;


    private PasswordEncoder passwordEncoder;


    private CloudStorageService amazonS3Service;


    private JwtService jwtService;


    private EmailService emailService;


    private RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, UserMapper mapper, PasswordEncoder passwordEncoder, CloudStorageService amazonS3Service,
                           JwtService jwtService, EmailService emailService, RoleService roleService){
        this.userRepo = userRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.amazonS3Service = amazonS3Service;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.roleService = roleService;
    }

    @Override
    public SingupResponse createUser(SignupRequest signupRequest, MultipartFile image) throws IOException, RegisterException, CloudStorageClientException, CorruptedFileException {
        Boolean userFound = userRepo.existsByEmail(signupRequest.getEmail());

        if(!userFound){
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
            emailService.sendEmail(user.getEmail(), GlobalConstants.TEMPLATE_WELCOME);

            SingupResponse singupResponse = new SingupResponse();
            singupResponse.setUser(mapper.userEntity2DTO(user));
            singupResponse.setMessage("Successful registration");
            singupResponse.setToken(jwtService.createToken(user));

            return singupResponse;
        }else{
            throw new RegisterException("The email is already in use");
        }
    }

    @Override
    public SingupResponse createUser(SignupRequest signupRequest) throws IOException, CloudStorageClientException, CorruptedFileException {
        Boolean userFound = userRepo.existsByEmail(signupRequest.getEmail());

        if(!userFound){
            User user = new User();

            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

            if(signupRequest.getEncoded_image() != null){
                user.setPhoto(amazonS3Service.uploadBase64File(
                        signupRequest.getEncoded_image().getEncoded_string(),
                        signupRequest.getEncoded_image().getFile_name()
                ));
            }

            user.setRoleId(roleService.getRoleUser());

            save(user);
            emailService.sendEmail(user.getEmail(), GlobalConstants.TEMPLATE_WELCOME);

            SingupResponse singupResponse = new SingupResponse();
            singupResponse.setUser(mapper.userEntity2DTO(user));
            singupResponse.setMessage("Successful registration");
            singupResponse.setToken(jwtService.createToken(user));

            return singupResponse;
        }else{
            throw new RegisterException("The email is already in use");
        }
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
    public UserDTO updateUser(MultipartFile file, UserDTORequest userDTOrequest) throws Exception {
        Boolean exists = existsById(userDTOrequest.getId());
        if (!exists) throw new NotFoundException("A user with id " + userDTOrequest.getId() + " was not found");
        User user = userRepo.getById(userDTOrequest.getId());

        if (!userDTOrequest.getEmail().isEmpty()){
            Boolean emailExists= userRepo.existsByEmail(userDTOrequest.getEmail());
            if(emailExists)throw new Exception("The email is already in use");
            user.setEmail(userDTOrequest.getEmail());
        }
        if (!userDTOrequest.getFirstName().isEmpty()) user.setFirstName(userDTOrequest.getFirstName());
        if (!userDTOrequest.getLastName().isEmpty()) user.setLastName(userDTOrequest.getLastName());
        if (!userDTOrequest.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userDTOrequest.getPassword()));
        if (!file.isEmpty()) user.setPhoto(amazonS3Service.uploadFile(file));

        save(user);

        return mapper.userEntity2DTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTORequest userDTOrequest) throws Exception {
        Boolean exists = existsById(userDTOrequest.getId());
        if (!exists) throw new NotFoundException("A user with id " + userDTOrequest.getId() + " was not found");
        User user = userRepo.getById(userDTOrequest.getId());

        if (!userDTOrequest.getEmail().isEmpty()){
            Boolean emailExists= userRepo.existsByEmail(userDTOrequest.getEmail());
            if(emailExists)throw new Exception("The email is already in use");
            user.setEmail(userDTOrequest.getEmail());
        }
        if (!userDTOrequest.getFirstName().isEmpty()) user.setFirstName(userDTOrequest.getFirstName());
        if (!userDTOrequest.getLastName().isEmpty()) user.setLastName(userDTOrequest.getLastName());
        if (!userDTOrequest.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userDTOrequest.getPassword()));
        if (userDTOrequest.getEncoded_image() != null) user.setPhoto(amazonS3Service.uploadBase64File(
                userDTOrequest.getEncoded_image().getEncoded_string(),
                userDTOrequest.getEncoded_image().getFile_name()
        ));

        save(user);

        return mapper.userEntity2DTO(user);
    }

    public List<UserDTO> getAll() {

        List<UserDTO> usersDTO = new LinkedList<>();
        for(User user : userRepo.findAll()){
            usersDTO.add(mapper.userEntity2DTO(user));
        }
        usersDTO.sort(Comparator.comparing(UserDTO::getEmail));
        return usersDTO;

    }

    public UserDTO getMe(String jwt) throws Exception{
        String emailUser = jwtService.getUsername(jwt);
        Boolean exitsUser = userRepo.existsByEmail(emailUser);
        if (!exitsUser) throw new NotFoundException("A user with this token was not found");
        Optional<User> user = userRepo.findByEmail(emailUser);
        return mapper.userEntity2DTO(user.get());

    }

    @Override
    public boolean existsById(String id) {
        return this.userRepo.existsById(id);
    }

}
