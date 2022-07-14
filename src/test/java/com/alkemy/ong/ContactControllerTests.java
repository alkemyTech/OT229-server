package com.alkemy.ong;

import com.alkemy.ong.controllers.ContactController;
import com.alkemy.ong.dto.ContactDTO;
import com.alkemy.ong.services.ContactService;
import com.alkemy.ong.utility.GlobalConstants;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.security.RunAs;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
public class ContactControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ContactService service;

    @BeforeEach
    private void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public ContactDTO createContact(){
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setEmail("bren@gmail.com");
        contactDTO.setName("Bren");
        contactDTO.setPhone(11635478L);
        contactDTO.setMessage("Hello");
        return contactDTO;
    }
    @Test
    void testGetAll() throws Exception {
        ContactDTO contactDTO1 = createContact();
        ContactDTO contactDTO2 = createContact();

        when(service.getAll()).thenReturn(Arrays.asList(contactDTO1,contactDTO2));

        mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.CONTACT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)));

    }
}
