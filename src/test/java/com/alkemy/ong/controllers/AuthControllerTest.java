package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.exception.RegisterException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.controller.AuthController;
import com.alkemy.ong.security.payload.LoginRequest;
import com.alkemy.ong.security.payload.LoginResponse;
import com.alkemy.ong.security.payload.SignupRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
//@Import({SwaggerConfiguration.class})
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

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.AuthControllerTest#generateRequestMissingMandatoryAttributes")
        @DisplayName("Mandatory attributes missing")
        void test3(SignupRequest signupRequest) throws Exception{
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
        @MethodSource("com.alkemy.ong.controllers.AuthControllerTest#generateRequestWithBrokenMandatoryAttributes")
        @DisplayName("Broken attributes")
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
    }

    @Nested
    class loginUserTest{
        @Test
        @DisplayName("Valid case")
        void test1() throws Exception{
            LoginRequest request = generateLoginRequest();
            LoginResponse expectedResponse = generateLoginResponse();

            Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(expectedResponse));

            //Mockito.when(authenticationService.getAuthenticatedUser()).thenReturn(Optional.of(expectedResponse));

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.LOGIN)
                        .content(jsonMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(authenticationService).getAuthenticatedUser();
            //Mockito.verify(authenticationService).getAuthenticatedUser();
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

    private static UserDTO generateUserDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jhon");
        userDTO.setLastName("Doe");
        userDTO.setEmail("jhondoe@mockito.mock");
        userDTO.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657487950406-userImage.png");

        return userDTO;
    }

    private static List<SignupRequest> generateRequestMissingMandatoryAttributes(){
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

    private static List<SignupRequest> generateRequestWithBrokenMandatoryAttributes(){
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
}
