package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(UserController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    JwtService jwtService; // TO MOCK CREDENTIALS

    @MockBean
    UserDetailsServiceImpl userDetailsService; // NOT USED. ADDED JUST TO DEAL WITH SECURITY DEPENDENCY LOADING ISSUES.

    @MockBean
    private UserService userService;

    ObjectMapper jsonMapper = new ObjectMapper();


    @Nested
    class getAllUserTest{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception{
            List<UserDTO> expectedResponse = generateListUserDTOResponse();

            Mockito.when(userService.getAll()).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.USER))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).getAll();
        }

        @Test
        @DisplayName("Invalid user role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test2() throws Exception{
            List<UserDTO> expectedResponse = generateListUserDTOResponse();

            Mockito.when(userService.getAll()).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.USER))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).getAll();
        }

        @Test
        @DisplayName("No token provided")
        void test3() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            List<UserDTO> expectedResponse = generateListUserDTOResponse();
            Mockito.when(userService.getAll()).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.USER))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).getAll();
        }

        @Test
        @DisplayName("Token not valid")
        void test4() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            List<UserDTO> expectedResponse = generateListUserDTOResponse();
            Mockito.when(userService.getAll()).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.USER))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).getAll();
        }
    }

    @Nested
    class updateUserTest{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws  Exception{
            UserDTORequest request = generateUserDTORequest();
            UserDTO expectedResponse = generateUserDTO();

            Mockito.when(userService.updateUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
            .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).updateUser(Mockito.any());
        }

        @Test
        @DisplayName("User not found")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws  Exception{
            UserDTORequest request = generateUserDTORequest();

            Mockito.when(userService.updateUser(Mockito.any())).thenThrow(new NotFoundException("User not found"));

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).updateUser(Mockito.any());
        }

        @Test
        @DisplayName("User not found")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test3() throws  Exception{
            UserDTORequest request = generateUserDTORequest();

            Mockito.when(userService.updateUser(Mockito.any())).thenThrow(new Exception("The email is already in use"));

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).updateUser(Mockito.any());
        }

        @Test
        @DisplayName("Invalid user role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws  Exception{
            UserDTORequest request = generateUserDTORequest();
            UserDTO expectedResponse = generateUserDTO();

            Mockito.when(userService.updateUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).updateUser(Mockito.any());
        }

        @Test
        @DisplayName("No token provided")
        void test5() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            UserDTORequest request = generateUserDTORequest();
            UserDTO expectedResponse = generateUserDTO();
            Mockito.when(userService.updateUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).updateUser(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void test6() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            UserDTORequest request = generateUserDTORequest();
            UserDTO expectedResponse = generateUserDTO();
            Mockito.when(userService.updateUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).updateUser(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.UserControllerTest#generateRequestMissingAttributes")
        @DisplayName("Mandatory attributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test7(UserDTORequest userDTORequest) throws Exception{
            UserDTO expectedResponse = generateUserDTO();

            Mockito.when(userService.updateUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                            .content(jsonMapper.writeValueAsString(userDTORequest))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).updateUser(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.UserControllerTest#generateRequestWithBrokenAttributes")
        @DisplayName("Invalid attribute format")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test8(UserDTORequest userDTORequest) throws Exception{
            UserDTO expectedResponse = generateUserDTO();

            Mockito.when(userService.updateUser(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.USER)
                    .content(jsonMapper.writeValueAsString(userDTORequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Jhon"))))
            .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).updateUser(Mockito.any());
        }
    }

    @Nested
    class deleteUserTest{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception{
            String idParam = "?id=123";
            String expectedResponse = "Successfully deleted user";

            Mockito.when(userService.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER + idParam))
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andExpect(MockMvcResultMatchers.content().string(expectedResponse))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).delete(Mockito.any());
        }

        @Test
        @DisplayName("User not found")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws Exception{
            String idParam = "?id=123";
            String expectedResponse = "Successfully deleted user";

            Mockito.when(userService.delete(Mockito.any())).thenThrow(new NotFoundException("A user with id provided was not found"));

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER + idParam))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService).delete(Mockito.any());
        }

        @Test
        @DisplayName("Invalid param")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test3() throws Exception{
            String idParam = "?idd=123";
            String expectedResponse = "Successfully deleted user";

            Mockito.when(userService.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER + idParam))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).delete(Mockito.any());
        }

        @Test
        @DisplayName("Invalid user role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws Exception{
            String idParam = "?id=123";
            String expectedResponse = "Successfully deleted user";

            Mockito.when(userService.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER + idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).delete(Mockito.any());
        }

        @Test
        @DisplayName("No token provided")
        void test5() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            String idParam = "?id=123";
            String expectedResponse = "Successfully deleted user";
            Mockito.when(userService.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER + idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).delete(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void test6() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            String idParam = "?id=123";
            String expectedResponse = "Successfully deleted user";
            Mockito.when(userService.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER + idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(userService, Mockito.never()).delete(Mockito.any());
        }
    }

    private static UserDTO generateUserDTO(){
        UserDTO user = new UserDTO();
        user.setFirstName("Jhon");
        user.setLastName("Doe");
        user.setEmail("jhondoe@mockito.mock");
        user.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657487950406-userImage.png");

        return user;
    }

    private static UserDTORequest generateUserDTORequest(){
        UserDTORequest userDTORequest = new UserDTORequest();
        userDTORequest.setId("abc123");
        userDTORequest.setFirstName("Jhon");
        userDTORequest.setLastName("Doe");
        userDTORequest.setEmail("jhondoe@mockito.mock");
        userDTORequest.setPassword("1234");
        userDTORequest.setEncoded_image(new EncodedImageDTO(
                "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                "sample.png"
        ));

        return userDTORequest;
    }

    private static List<UserDTO> generateListUserDTOResponse(){
        List<UserDTO> listOfUsers = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            listOfUsers.add(generateUserDTO());
        }

        return listOfUsers;
    }

    private static List<UserDTORequest> generateRequestWithBrokenAttributes(){
        List<UserDTORequest> listOfRequest = new ArrayList<>();
        UserDTORequest userDTORequest;

        // Case 1: Broken id
        userDTORequest = generateUserDTORequest();
        userDTORequest.setId("");
        listOfRequest.add(userDTORequest);

        // Case 2: Broken First name
        userDTORequest = generateUserDTORequest();
        userDTORequest.setFirstName("");
        listOfRequest.add(userDTORequest);

        // Case 3: Broken Last name
        userDTORequest = generateUserDTORequest();
        userDTORequest.setLastName("");
        listOfRequest.add(userDTORequest);

        // Case 4: Broken email
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEmail("");
        listOfRequest.add(userDTORequest);

        // Case 5: Bad format email
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEmail("jhondoeAmockito.mock");
        listOfRequest.add(userDTORequest);

        // Case 6: Broken password
        userDTORequest = generateUserDTORequest();
        userDTORequest.setPassword("");
        listOfRequest.add(userDTORequest);

        // Case 7: Broken name image
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEncoded_image(new EncodedImageDTO(
                "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                ""
        ));
        listOfRequest.add(userDTORequest);

        // Case 8: Broken endoded image
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEncoded_image(new EncodedImageDTO(
                "",
                "sample.png"
        ));
        listOfRequest.add(userDTORequest);

        return listOfRequest;
    }

    private static List<UserDTORequest> generateRequestMissingAttributes(){
        List<UserDTORequest> listOfRequest = new ArrayList<>();
        UserDTORequest userDTORequest;

        // Case 1: Missing id
        userDTORequest = generateUserDTORequest();
        userDTORequest.setId(null);
        listOfRequest.add(userDTORequest);

        // Case 2: Missing First name
        userDTORequest = generateUserDTORequest();
        userDTORequest.setFirstName(null);
        listOfRequest.add(userDTORequest);

        // Case 3: Missing Last name
        userDTORequest = generateUserDTORequest();
        userDTORequest.setLastName(null);
        listOfRequest.add(userDTORequest);

        // Case 4: Missing email
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEmail(null);
        listOfRequest.add(userDTORequest);

        // Case 5: Missing password
        userDTORequest = generateUserDTORequest();
        userDTORequest.setPassword(null);
        listOfRequest.add(userDTORequest);

        // Case 6: Missing name image
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEncoded_image(new EncodedImageDTO(
                "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                null
        ));
        listOfRequest.add(userDTORequest);

        // Case 7: Missing endoded image
        userDTORequest = generateUserDTORequest();
        userDTORequest.setEncoded_image(new EncodedImageDTO(
                null,
                "sample.png"
        ));
        listOfRequest.add(userDTORequest);

        return listOfRequest;
    }
}
