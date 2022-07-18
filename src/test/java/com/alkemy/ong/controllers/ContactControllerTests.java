package com.alkemy.ong.controllers;


import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.*;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.ContactService;
import com.alkemy.ong.services.SlidesService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest(ContactController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class ContactControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    JwtService jwtService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    private ContactService service;
    @MockBean
    private SlidesService slidesService;

    ObjectMapper jsonMapper = new ObjectMapper();

    @Nested
    class testGetAllContacts {

        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetAllContactsWithValidToken() throws Exception {


            List<ContactDTO> mockContactDTOList = ContactControllerTests.generateMockContactDtoList();
            Mockito.when(service.getAll()).thenReturn(mockContactDTOList);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.CONTACT))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(mockContactDTOList)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).getAll();

        }

        @Test
        @DisplayName("No token provided")
        void testGetAllContactsWithoutToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            List<ContactDTO> mockContactDTOList = ContactControllerTests.generateMockContactDtoList();
            Mockito.when(service.getAll()).thenReturn(mockContactDTOList);


            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.CONTACT))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(service, Mockito.never()).getAll();
        }

        @Test
        @DisplayName("Token not valid")
        void testGetAllContactsWithInvalidToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            List<ContactDTO> mockContactDTOList = ContactControllerTests.generateMockContactDtoList();
            Mockito.when(service.getAll()).thenReturn(mockContactDTOList);


            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.CONTACT))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());


            Mockito.verify(service, Mockito.never()).getAll();
        }

    }

    @Nested
    class testCreateContact {

        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testCreateContactWithCorrectAttributes() throws Exception {


            ContactDTORequest request = ContactControllerTests.createMockContactDTORequest();
            ContactDTOResponse response = ContactControllerTests.createMockContactDTOResponse();
            Mockito.when(service.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(response)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).create(Mockito.any());
        }

        @Test
        @DisplayName("No token provided")
        void testCreateContactWithoutToken() throws Exception {


            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            ContactDTORequest request = ContactControllerTests.createMockContactDTORequest();
            ContactDTOResponse response = ContactControllerTests.createMockContactDTOResponse();
            Mockito.when(service.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void testCreateContactWithInvalidToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            ContactDTORequest request = ContactControllerTests.createMockContactDTORequest();
            ContactDTOResponse response = ContactControllerTests.createMockContactDTOResponse();
            Mockito.when(service.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }


        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.ContactControllerTests#generateRequestsWithBrokenAttributes")
        @DisplayName("Invalid attribute format")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testCreateContactWithBrokenAttributes(ContactDTORequest requestWithBrokenAttribute) throws Exception {

            ContactDTOResponse response = ContactControllerTests.createMockContactDTOResponse();
            Mockito.when(service.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }
    }


    static ContactDTO createMockContactDTO() {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setEmail("pepe@gmail.com");
        contactDTO.setName("Pepe");
        contactDTO.setPhone(11635478L);
        contactDTO.setMessage("Hello");
        return contactDTO;
    }

    static ContactDTORequest createMockContactDTORequest() {
        ContactDTORequest request = new ContactDTORequest();
        request.setName("pepe");
        request.setEmail("pepe@gmail.com");
        request.setPhone(11635478L);
        request.setMessage("Hello");
        return request;
    }

    static ContactDTOResponse createMockContactDTOResponse() {
        ContactDTOResponse response = new ContactDTOResponse();
        response.setName("pepe");
        response.setEmail("pepe@gmail.com");
        response.setConfirmation("ok");
        return response;
    }

    static List<ContactDTO> generateMockContactDtoList() {
        ContactDTO dto = new ContactDTO();
        dto.setEmail("mock@mockemail.com");
        dto.setPhone(1168955L);
        dto.setMessage("Hello mocks!");
        dto.setName("Mock");
        return Collections.singletonList(dto);
    }


    static List<ContactDTORequest> generateRequestsWithBrokenAttributes() {
        List<ContactDTORequest> requestList = new ArrayList<>();
        ContactDTORequest dto;

        dto = ContactControllerTests.createMockContactDTORequest();
        dto.setName("");
        requestList.add(dto);

        dto = ContactControllerTests.createMockContactDTORequest();
        dto.setEmail("");
        requestList.add(dto);

        dto = ContactControllerTests.createMockContactDTORequest();
        dto.setEmail("invalid format email");
        requestList.add(dto);


        return requestList;
    }

}
