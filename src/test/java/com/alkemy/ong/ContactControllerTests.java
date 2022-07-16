package com.alkemy.ong;


import com.alkemy.ong.dto.ContactDTO;
import com.alkemy.ong.dto.ContactDTORequest;
import com.alkemy.ong.dto.ContactDTOResponse;
import com.alkemy.ong.services.ContactService;
import com.alkemy.ong.utility.GlobalConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

@AutoConfigureMockMvc
@SpringBootTest
public class ContactControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ContactService service;
    @Autowired
    private ObjectMapper objectMapper;

    private ContactDTO contactDTO;
    private ContactDTORequest contactDTORequest;
    private ContactDTOResponse contactDTOResponse;

    @BeforeEach
    private void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        contactDTO = createContactDTO();
        contactDTORequest = createContactDTORequest();
        contactDTOResponse = createContactDTOResponse();
    }

    @Test
    void testEndpointGetAllContactsReturnlistOfContacts() throws Exception {
        ContactDTO contactDTO2 = createContactDTO();

        Mockito.when(service.getAll()).thenReturn(Arrays.asList(contactDTO, contactDTO2));

        mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.CONTACT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)));

    }

    @Test
    void testEndpointCreateContactWithCorrectAttributesReturnAContactDTOResponse() throws Exception {

        Mockito.when(service.create(contactDTORequest)).thenReturn(contactDTOResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTORequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }


    @Test
    void testEndpointCreateContactWithIncorrectAttributesNameAndEmailReturnABadRequest() throws Exception {

        contactDTORequest.setEmail("");
        contactDTORequest.setName("");

        Mockito.when(service.create(contactDTORequest)).thenThrow(HttpClientErrorException.BadRequest.class);

        mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTORequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void testEndpointCreateContactWithIncorrectAttributeEmailFormatReturnABadRequest() throws Exception {

        contactDTORequest.setEmail("pepe");

        Mockito.when(service.create(contactDTORequest)).thenThrow(HttpClientErrorException.BadRequest.class);

        mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.CONTACT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTORequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

   private ContactDTO createContactDTO() {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setEmail("pepe@gmail.com");
        contactDTO.setName("Pepe");
        contactDTO.setPhone(11635478L);
        contactDTO.setMessage("Hello");
        return contactDTO;
    }

  private ContactDTORequest createContactDTORequest() {
        ContactDTORequest request = new ContactDTORequest();
        request.setName("pepe");
        request.setEmail("pepe@gmail.com");
        request.setPhone(11635478L);
        request.setMessage("Hello");
        return request;
    }

   private ContactDTOResponse createContactDTOResponse() {
        ContactDTOResponse response = new ContactDTOResponse();
        response.setName("pepe");
        response.setEmail("pepe@gmail.com");
        response.setConfirmation("ok");
        return response;
    }
}