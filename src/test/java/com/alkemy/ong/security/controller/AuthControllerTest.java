package com.alkemy.ong.security.controller;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.exception.RegisterException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.payload.LoginRequest;
import com.alkemy.ong.security.payload.LoginResponse;
import com.alkemy.ong.security.payload.SignupRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.EmailService;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserDetailsServiceImpl userDetailsService; // NOT USED. ADDED JUST TO DEAL WITH SECURITY DEPENDENCY LOADING ISSUES.

    @MockBean
    JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    ObjectMapper jsonMapper = new ObjectMapper();

    @Nested
    class registerUserTest{
        @Test
        @DisplayName("Valid case")
        void test1() throws Exception{
            SignupRequest request = generateRegisterRequest();
            SingupResponse expectedResponse = generateRegisterResponse();

            Mockito.when(userService.createUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.REGISTER)
                        .content(jsonMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).createUser(Mockito.any());
        }

        @Test
        @DisplayName("Email is already in use")
        void test2() throws Exception{
            SignupRequest request = generateRegisterRequest();

            Mockito.when(userService.createUser(Mockito.any())).thenThrow(new RegisterException());

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.REGISTER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful registration"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).createUser(Mockito.any());
        }

        @Test
        @DisplayName("Error sending mail")
        void test3() throws Exception{
            SignupRequest request = generateRegisterRequest();

            Mockito.when(userService.createUser(Mockito.any())).thenThrow(new IOException());

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.REGISTER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadGateway())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful registration"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).createUser(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.security.controller.AuthControllerTest#generateRegisterRequestMissingMandatoryAttributes")
        @DisplayName("Mandatory attributes missing")
        void test4(SignupRequest signupRequest) throws Exception{
            SingupResponse expectedResponse = generateRegisterResponse();
            Mockito.when(userService.createUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.REGISTER)
                        .content(jsonMapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful registration"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).createUser(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.security.controller.AuthControllerTest#generateRegisterRequestWithBrokenMandatoryAttributes")
        @DisplayName("Broken attributes")
        void test5(SignupRequest signupRequest) throws Exception{
            SingupResponse expectedResponse = generateRegisterResponse();
            Mockito.when(userService.createUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.REGISTER)
                            .content(jsonMapper.writeValueAsString(signupRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful registration"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).createUser(Mockito.any());
        }
    }

    @Nested
    class loginUserTest{
        @Test
        @DisplayName("Valid case")
        void test1() throws Exception{
            LoginRequest request = generateLoginRequest();
            LoginResponse expectedResponse = generateLoginResponse();

            Mockito.when(authenticationService.authenticate(Mockito.any(), Mockito.any())).thenReturn(authenticationService);
            Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(expectedResponse));

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.LOGIN)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", request.getUsername())
                        .param("password", request.getPassword())
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(authenticationService).authenticate(Mockito.any(), Mockito.any());
            Mockito.verify(authenticationService).getAuthenticatedUser();
        }

        @Test
        @DisplayName("Internal Server Error")
        void test2() throws Exception{
            LoginRequest request = generateLoginRequest();

            Mockito.when(authenticationService.authenticate(Mockito.any(), Mockito.any())).thenReturn(authenticationService);
            Mockito.when(authenticationService.getAuthenticatedUser()).thenThrow(new IllegalStateException());

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.LOGIN)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", request.getUsername())
                            .param("password", request.getPassword())
                    )
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful Authentication"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(authenticationService).authenticate(Mockito.any(), Mockito.any());
            Mockito.verify(authenticationService).getAuthenticatedUser();
        }

        @Test
        @DisplayName("Incorrect credentials in login")
        void test3() throws Exception{
            LoginRequest request = generateLoginRequest();

            Mockito.when(authenticationService.authenticate(Mockito.any(), Mockito.any())).thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.LOGIN)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", request.getUsername())
                            .param("password", request.getPassword())
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful Authentication"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(authenticationService).authenticate(Mockito.any(), Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.security.controller.AuthControllerTest#generateLoginRequestMissingMandatoryAttributes") // cambiar el método
        @DisplayName("Mandatory atributes missing")
        void test4(LoginRequest loginRequest) throws Exception{
            LoginResponse expectedResponse = generateLoginResponse();
            Mockito.when(authenticationService.authenticate(Mockito.any(), Mockito.any())).thenReturn(authenticationService);
            Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(expectedResponse));

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.LOGIN)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", loginRequest.getUsername())
                            .param("password", loginRequest.getPassword())
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful Authentication"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(authenticationService, Mockito.never()).authenticate(Mockito.any(), Mockito.any());
            Mockito.verify(authenticationService, Mockito.never()).getAuthenticatedUser();
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.security.controller.AuthControllerTest#generateLoginRequestWithBrokenMandatoryAttributes") // cambiar el método
        @DisplayName("Mandatory atributes blank")
        void test5(LoginRequest loginRequest) throws Exception{
            LoginResponse expectedResponse = generateLoginResponse();
            Mockito.when(authenticationService.authenticate(Mockito.any(), Mockito.any())).thenReturn(authenticationService);
            Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(expectedResponse));

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.LOGIN)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", loginRequest.getUsername())
                            .param("password", loginRequest.getPassword())
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Successful Authentication"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(authenticationService, Mockito.never()).authenticate(Mockito.any(), Mockito.any());
            Mockito.verify(authenticationService, Mockito.never()).getAuthenticatedUser();
        }
    }

    @Nested
    class getMeTest{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test1() throws Exception{
            String requestJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            UserDTO expectedResponse = generateGetMeDTO();

            Mockito.when(userService.getMe(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.AUTH_ME)
                        .header("authorization", requestJwt)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).getMe(Mockito.any());
        }

        @Test
        @DisplayName("Not found user")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test2() throws Exception{
            String requestJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

            Mockito.when(userService.getMe(Mockito.any())).thenThrow(new NotFoundException("A user with this token was not found"));

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.AUTH_ME)
                            .header("authorization", requestJwt)
                    )
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).getMe(Mockito.any());
        }

        @Test
        @DisplayName("Invalid token")
        void test3() throws Exception{
            String requestJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.AUTH_ME)
                            .header("authorization", requestJwt)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("Broken token")
        void test4() throws Exception{
            String requestJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.AUTH_ME)
                            .header("authorization", requestJwt)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    private static SignupRequest generateRegisterRequest(){
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("Jhon");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("jhondoe@mockito.mock");
        signupRequest.setPassword("1234");
        signupRequest.setEncoded_image(
                new EncodedImageDTO("iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB", "sample.png")
        );

        return signupRequest;
    }

    private static SingupResponse generateRegisterResponse(){

        SingupResponse singupResponse = new SingupResponse();
        singupResponse.setUser(generateUserDTO());
        singupResponse.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        singupResponse.setMessage("Successful registration");

        return singupResponse;
    }

    private static LoginRequest generateLoginRequest(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("jhondoe@mockito.mock");
        loginRequest.setPassword("1234");

        return loginRequest;
    }

    private static LoginResponse generateLoginResponse(){

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUser(generateUserDTO());
        loginResponse.setMessage("Successful Authentication");
        loginResponse.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        return loginResponse;
    }

    private static UserDTO generateGetMeDTO(){
        return generateUserDTO();
    }

    private static UserDTO generateUserDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jhon");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jhondoe@mockito.mock");
        userDTO.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657487950406-userImage.png");

        return userDTO;
    }

    private static List<SignupRequest> generateRegisterRequestMissingMandatoryAttributes(){
        List<SignupRequest> request = new ArrayList<>();
        SignupRequest signup;

        // CASE 1: Missing first name
        signup = generateRegisterRequest();
        signup.setFirstName(null);
        request.add(signup);

        // CASE 2: Missing last name
        signup = generateRegisterRequest();
        signup.setLastName(null);
        request.add(signup);

        // CASE 3: Missing email
        signup = generateRegisterRequest();
        signup.setEmail(null);
        request.add(signup);

        // CASE 4: Missing password
        signup = generateRegisterRequest();
        signup.setLastName(null);
        request.add(signup);

        // CASE 5: Missing file name
        signup = generateRegisterRequest();
        signup.setEncoded_image(new EncodedImageDTO(
                "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB",
                null
        ));
        request.add(signup);

        // CASE 6: Missing endoded file
        signup = generateRegisterRequest();
        signup.setEncoded_image(new EncodedImageDTO(
                null,
                "sample.png"
        ));
        request.add(signup);

        return request;
    }

    private static List<SignupRequest> generateRegisterRequestWithBrokenMandatoryAttributes(){
        List<SignupRequest> request = new ArrayList<>();
        SignupRequest signup;

        // CASE 1: Blank first name
        signup = generateRegisterRequest();
        signup.setFirstName("");
        request.add(signup);

        // CASE 2: Blank last name
        signup = generateRegisterRequest();
        signup.setLastName("");
        request.add(signup);

        // CASE 3: Blank email
        signup = generateRegisterRequest();
        signup.setEmail("");
        request.add(signup);

        // CASE 4: Blank password
        signup = generateRegisterRequest();
        signup.setLastName("");
        request.add(signup);

        // CASE 5: Blank file name
        signup = generateRegisterRequest();
        signup.setEncoded_image(new EncodedImageDTO(
                "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB",
                ""
        ));
        request.add(signup);

        // CASE 6: Blank endoded file
        signup = generateRegisterRequest();
        signup.setEncoded_image(new EncodedImageDTO(
                "",
                "sample.png"
        ));
        request.add(signup);

        return request;
    }

    private static List<LoginRequest> generateLoginRequestMissingMandatoryAttributes(){
        List<LoginRequest> request = new ArrayList<>();
        LoginRequest loginRequest;

        // Case 1: Missing email
        loginRequest = generateLoginRequest();
        loginRequest.setUsername(null);
        request.add(loginRequest);

        // Case 2: Missing password
        loginRequest = generateLoginRequest();
        loginRequest.setPassword(null);
        request.add(loginRequest);

        return request;
    }


    private static List<LoginRequest> generateLoginRequestWithBrokenMandatoryAttributes(){
        List<LoginRequest> request = new ArrayList<>();
        LoginRequest loginRequest;

        // Case 1: Missing email
        loginRequest = generateLoginRequest();
        loginRequest.setUsername("");
        request.add(loginRequest);

        // Case 2 bad email format
        loginRequest = generateLoginRequest();
        loginRequest.setUsername("jhonAgmail.com");
        request.add(loginRequest);

        // Case 3: Missing password
        loginRequest = generateLoginRequest();
        loginRequest.setPassword("");
        request.add(loginRequest);

        return request;
    }
}
