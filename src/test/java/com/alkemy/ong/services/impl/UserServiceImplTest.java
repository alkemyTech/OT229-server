package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.EncodedImageDTO;
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
import com.alkemy.ong.utility.GlobalConstants;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
public class UserServiceImplTest {
    private static UserMapper userMapper = new UserMapper();

    @Autowired
    private UserRepository userRepository;

    private static final int numberOfMockUser = 5;

    private static String existUserById;

    @BeforeEach
    @Transactional
    void populateDatabase() {
        User user = new User();

        for (int i = 1; i <= numberOfMockUser; i++) {
            user = userRepository.save(generateMockUser(i));
        }

        existUserById = user.getId();
    }

    @AfterEach
    @Transactional
    void emptyDatabase() {
        userRepository.deleteAll();
    }

    @Nested
    class createUserTest {
        @Nested
        class WithMultiPartFile {
            @Test
            @DisplayName("Valid case")
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
                try {
                    Mockito.verify(mockCloudStorageService).uploadFile(mockImageFile);
                    Mockito.verify(passwordEncoder).encode(request.getPassword());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Email is already in use")
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
                request.setEmail("userMock2@mockito.mock");

                assertThrows(
                        RegisterException.class,
                        () -> {
                            SingupResponse result = userService.createUser(request, mockImageFile);
                        }
                        ,"Expected exception thrown"
                );
                try {
                    Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(mockImageFile);
                    Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Nested
        class WithImageBase64Encoded{
            @Test
            @DisplayName("Valid case")
            void test1() throws Exception {
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
                Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$naDtwtvaKpX0h.fzMvYJfe4jW8EkewCG7qUoISue6EtJ1GdCVoOHe");

                assertDoesNotThrow(
                        () -> {
                            SingupResponse result = userService.createUser(request);
                            assertNotNull(result, "Result object is not null.");
                            assertEquals(mockUploadedFileUrl, result.getUser().getImage(), "The image attribute was accurately updated.");
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
            @DisplayName("Email is already in use")
            void test2() throws Exception {
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
                request.setEmail("userMock2@mockito.mock");

                assertThrows(
                        RegisterException.class,
                        () -> {
                            SingupResponse result = userService.createUser(request);
                        }
                        ,"Expected exception thrown"
                );
                try {
                    Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
                    Mockito.verify(passwordEncoder, Mockito.never()).encode(request.getPassword());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static User generateMockUser(int indexStamp) {
        User user = new User();
        user.setId("");
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
}
