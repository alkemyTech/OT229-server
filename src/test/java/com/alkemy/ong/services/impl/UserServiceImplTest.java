package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.EncodedImageDTO;
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
import com.alkemy.ong.security.service.impl.JwtServiceImpl;
import com.alkemy.ong.services.CloudStorageService;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {
    private static UserMapper userMapper = new UserMapper();

    @Autowired
    private UserRepository userRepository;

    private Stack<String> idEntities = new Stack<>();

    private Stack<String> emailEntities = new Stack<>();
    private static int numberOfMockUser = 20;


    @BeforeAll
    @Transactional
    void populateDatabase() {
        User user = new User();

        for (int i = 1; i <= numberOfMockUser; i++) {
            user = userRepository.save(generateMockUser(i));
            idEntities.push(user.getId());
            emailEntities.push(user.getEmail());
        }
    }


    @Transactional
    void emptyDatabase() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Create user with multipart file - Valid case")
    @Order(1)
    void test1() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        SignupRequest request = generateSignupRequest();
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$naDtwtvaKpX0h.fzMvYJfe4jW8EkewCG7qUoISue6EtJ1GdCVoOHe");

        assertDoesNotThrow(
                () -> {
                    SingupResponse result = userService.createUser(request, mockImageFile);
                    assertNotNull(result, "Result object is not null.");
                    assertEquals(mockUploadedFileUrl, result.getUser().getImage(), "The image attribute was accurately updated.");
                }
                , "The service did not throw any exception."
        );
        numberOfMockUser++;
        try {
            Mockito.verify(mockCloudStorageService).uploadFile(mockImageFile);
            Mockito.verify(passwordEncoder).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Create user with multipart file - Email is already in use")
    @Order(2)
    void test2() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        SignupRequest request = generateSignupRequest();
        request.setEmail(emailEntities.pop());

        assertThrows(
                RegisterException.class,
                () -> {
                    SingupResponse result = userService.createUser(request, mockImageFile);
                }
                , "Expected exception thrown"
        );
        try {
            Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(mockImageFile);
            Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Create user with encoded image - Valid case")
    @Order(3)
    void test3() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String mockEncodedImageFileContent = "MockEncodedImageFileContent";
        String mockEncodedImageFileName = "test_file.mock";
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                    .thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
        SignupRequest request = generateSignupRequest();
        request.setEmail("newEmail@mockito.mock");

        request.setEncoded_image(mockEncodedImage);
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$naDtwtvaKpX0h.fzMvYJfe4jW8EkewCG7qUoISue6EtJ1GdCVoOHe");

        assertDoesNotThrow(
                () -> {
                    SingupResponse result = userService.createUser(request);
                    assertNotNull(result, "Result object is not null.");
                    assertEquals(mockUploadedFileUrl, result.getUser().getImage(), "The image attribute was accurately updated.");
                }
                , "The service did not throw any exception."
        );
        numberOfMockUser++;
        try {
            Mockito.verify(mockCloudStorageService).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
            Mockito.verify(passwordEncoder).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Create user with encoded image - Email is already in use")
    @Order(4)
    void test4() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String mockEncodedImageFileContent = "MockEncodedImageFileContent";
        String mockEncodedImageFileName = "test_file.mock";
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                    .thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
        SignupRequest request = generateSignupRequest();

        request.setEncoded_image(mockEncodedImage);
        request.setEmail(emailEntities.pop());

        assertThrows(
                RegisterException.class,
                () -> {
                    SingupResponse result = userService.createUser(request);
                }
                , "Expected exception thrown"
        );
        try {
            Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
            Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update user with multipart file - Valid case")
    @Transactional // Sin esta anotación no anda
    @Order(5)
    void test5() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        UserDTORequest request = generateUserDTORequest();
        request.setId(idEntities.pop());
        request.setEmail("newEmail3271@mockito.mock");

        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$naDtwtvaKpX0h.fzMvYJfe4jW8EkewCG7qUoISue6EtJ1GdCVoOHe");

        assertDoesNotThrow(
                () -> {
                    UserDTO result = userService.updateUser(mockImageFile, request);
                    assertNotNull(result, "Result object is not null.");
                }
                , "The service did not throw any exception."
        );
        try {
            Mockito.verify(mockCloudStorageService).uploadFile(mockImageFile);
            Mockito.verify(passwordEncoder).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update user with multipart file - User not found")
    @Transactional // Sin esta anotación no anda
    @Order(6)
    void test6() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        UserDTORequest request = generateUserDTORequest();
        request.setId("NotFoundID");

        assertThrows(
                NotFoundException.class,
                () -> {
                    UserDTO result = userService.updateUser(mockImageFile, request);
                }
                , "Expected exception thrown"
        );
        try {
            Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(mockImageFile);
            Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update user with multipart file - Email is already in use")
    @Transactional // Sin esta anotación no anda
    @Order(7)
    void test7() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        UserDTORequest request = generateUserDTORequest();
        request.setId(idEntities.pop());
        request.setEmail(emailEntities.pop());

        assertThrows(
                Exception.class,
                () -> {
                    UserDTO result = userService.updateUser(mockImageFile, request);
                }
                , "Expected exception thrown"
        );
        try {
            Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(mockImageFile);
            Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update user with endoced image - Valid case")
    @Transactional // Sin esta anotación no anda
    @Order(8)
    void test8() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String mockEncodedImageFileContent = "MockEncodedImageFileContent";
        String mockEncodedImageFileName = "test_file.mock";
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                    .thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
        UserDTORequest request = generateUserDTORequest();
        request.setId(idEntities.pop());
        request.setEmail("newEmail390@mockito.mock");
        request.setEncoded_image(mockEncodedImage);

        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$naDtwtvaKpX0h.fzMvYJfe4jW8EkewCG7qUoISue6EtJ1GdCVoOHe");

        assertDoesNotThrow(
                () -> {
                    UserDTO result = userService.updateUser(request);
                    assertNotNull(result, "Result object is not null.");
                }
                , "The service did not throw any exception."
        );
        try {
            Mockito.verify(mockCloudStorageService).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
            Mockito.verify(passwordEncoder).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update user with endoced image - User not found")
    @Transactional // Sin esta anotación no anda
    @Order(9)
    void test9() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String mockEncodedImageFileContent = "MockEncodedImageFileContent";
        String mockEncodedImageFileName = "test_file.mock";
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                    .thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
        UserDTORequest request = generateUserDTORequest();
        request.setId("NotFoundID");
        request.setEncoded_image(mockEncodedImage);

        assertThrows(
                NotFoundException.class,
                () -> {
                    UserDTO result = userService.updateUser(request);
                }
                , "Expected exception thrown"
        );
        try {
            Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
            Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update user with endoced image - Email is already in use")
    @Transactional // Sin esta anotación no anda
    @Order(10)
    void test10() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String mockEncodedImageFileContent = "MockEncodedImageFileContent";
        String mockEncodedImageFileName = "test_file.mock";
        String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

        try {
            Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                    .thenReturn(mockUploadedFileUrl);
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }

        EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
        UserDTORequest request = generateUserDTORequest();
        request.setId(idEntities.pop());
        request.setEmail(emailEntities.pop());
        request.setEncoded_image(mockEncodedImage);

        assertThrows(
                Exception.class,
                () -> {
                    UserDTO result = userService.updateUser(request);
                }
                , "Expected exception thrown"
        );
        try {
            Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
            Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
        } catch (CorruptedFileException | CloudStorageClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Delete user - Valid case")
    @Order(11)
    void test11() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);


        String id = idEntities.pop();
        String response = "Successfully deleted user with id " + id;

        assertDoesNotThrow(
                () -> {
                    String result = userService.delete(id);
                    assertEquals(result, response, "Test successful");
                }
                , "The service did not throw any exception."
        );
        numberOfMockUser--;
    }

    @Test
    @DisplayName("Delete user - Not found user")
    @Order(12)
    void test12() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);


        String id = "notFoundUser";

        assertThrows(
                NotFoundException.class,
                () -> {
                    String result = userService.delete(id);
                }
                , "Expected exception thrown"
        );
    }

    @Test
    @DisplayName("Get user by email - Valid case")
    @Order(13)
    void test13() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String email = emailEntities.pop();

        assertDoesNotThrow(
                () -> {
                    Optional<User> result = userService.getUserByEmail(email);
                    assertNotNull(result, "Test successful");
                }
                , "The service did not throw any exception."
        );
    }

    @Test
    @DisplayName("Get user by email - User not found by email")
    @Order(14)
    void test14() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String email = "userNotFoundByEmail@mockito.mock";

        assertDoesNotThrow(
                () -> {
                    Optional<User> result = userService.getUserByEmail(email);
                    assertTrue(result.isEmpty(), "Test successful");
                }
                , "The service did not throw any exception."
        );
    }

    @Test
    @DisplayName("Get all - Populated list returned")
    @Order(15)
    void test15() {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        List<UserDTO> resultList = userService.getAll();
        assertEquals(numberOfMockUser, resultList.size(), "The expected number of results were returned.");
    }

    @Test
    @DisplayName("Get all - Empty list returned")
    @Order(18)
    void test18() {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        emptyDatabase();
        assertDoesNotThrow(
                () -> {
                    List<UserDTO> resultList = userService.getAll();
                    assertEquals(0, resultList.size(), "Zero results were returned from the repository.");
                }
                , "Service did not throw an exception when receiving and empty list from the repository."
        );
    }

    @Test
    @DisplayName("Get me - Valid case")
    @Order(16)
    void test16() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String email = emailEntities.pop();

        Mockito.when(jwtService.getUsername(Mockito.any())).thenReturn(email);

        assertDoesNotThrow(
                () -> {
                    UserDTO result = userService.getMe(email);
                    assertNotNull(result, "Result object is not null.");
                }
                , "The service did not throw any exception."
        );
    }

    @Test
    @DisplayName("Get me - Not found")
    @Order(17)
    void test17() throws Exception {
        CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleServiceImpl roleService = Mockito.mock(RoleServiceImpl.class);
        EmailServiceImp emailService = Mockito.mock(EmailServiceImp.class);
        JwtServiceImpl jwtService = Mockito.mock(JwtServiceImpl.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, mockCloudStorageService,
                jwtService, emailService, roleService);

        String email = "userNotFound@mockito.mock";

        Mockito.when(jwtService.getUsername(Mockito.any())).thenReturn(email);

        assertThrows(
                NotFoundException.class,
                () -> {
                    UserDTO result = userService.getMe(email);
                }
                , "Expected exception thrown"
        );
    }

    private static User generateMockUser(int indexStamp) {
        User user = new User();
        user.setFirstName("User Firstname " + indexStamp);
        user.setLastName("User Lastname " + indexStamp);
        user.setPassword("1234");
        user.setEmail("userMock" + indexStamp + "@mockito.mock");
        user.setPhoto("image.jpg " + indexStamp);
        return user;
    }

    private static SignupRequest generateSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setFirstName("Jhon");
        request.setLastName("Doe");
        request.setEmail("jhondoe@mockito.mock");
        request.setPassword("1234");
        request.setEncoded_image(null);

        return request;
    }

    private static UserDTORequest generateUserDTORequest() {
        UserDTORequest userDTO = new UserDTORequest();
        userDTO.setId("id");
        userDTO.setFirstName("Jhon");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jhondoe@mockito.mock");
        userDTO.setPassword("1234");
        userDTO.setEncoded_image(null);

        return userDTO;
    }
}
