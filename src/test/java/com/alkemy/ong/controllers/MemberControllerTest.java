package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.exception.MemberNotFoundException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
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

import java.util.Collections;
import java.util.List;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(MemberController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    JwtService jwtService;

    @MockBean
    MemberService memberService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    ObjectMapper jsonMapper;

    @BeforeAll
    public void settings() {
        this.jsonMapper = new ObjectMapper();
    }

    static List<MemberDTORequest> generateDTOListRequest() {
        MemberDTORequest dto = new MemberDTORequest();
        dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657462814446-ezeTest.txt");
        dto.setDescription("TestDescription");
        dto.setName("TestName");
        dto.setFacebookUrl("TestFacebookUrl");
        dto.setInstagramUrl("TestInstagramUrl");
        dto.setLinkedinUrl("TestLinkedinUrl");

        return Collections.singletonList(dto);
    }

    static List<MemberDTOResponse> generateDTOList() {
        MemberDTOResponse dto = new MemberDTOResponse();
        dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657462814446-ezeTest.txt");
        dto.setDescription("TestDescription");
        dto.setName("TestName");
        dto.setFacebookUrl("TestFacebookUrl");
        dto.setInstagramUrl("TestInstagramUrl");
        dto.setLinkedinUrl("TestLinkedinUrl");

        return Collections.singletonList(dto);
    }

    static List<MemberDTORequest> generateRequestWithMissingMandatoryAttribute() {
        List<MemberDTORequest> list = generateDTOListRequest();
        list.get(0).setName(null);

        return list;
    }

    @Nested
    class getAll {

        @Test
        @DisplayName("Valid Case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test_1() throws Exception {
            PageResultResponse<MemberDTOResponse> response = new PageResultResponse();
            List<MemberDTOResponse> memberResultList = MemberControllerTest.generateDTOList();
            response.setContent(memberResultList);


            Mockito.when(memberService.getAllMembers(0)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.MEMBERS).param("page", "0"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(response)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).getAllMembers(0);
        }

        @Test
        @DisplayName("No Token Provided")
        void test_2() throws Exception {

        Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

        PageResultResponse<MemberDTOResponse> response = new PageResultResponse();
        List<MemberDTOResponse> memberResultList = MemberControllerTest.generateDTOList();
        response.setContent(memberResultList);

        mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.MEMBERS).param("page", "0"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                .andDo(MockMvcResultHandlers.print());

        Mockito.verify(memberService, Mockito.never()).getAllMembers(0);
        }

        @Test
        @DisplayName("Token not Valid")
        void test_3() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            PageResultResponse<MemberDTOResponse> response = new PageResultResponse();
            List<MemberDTOResponse> memberResultList = MemberControllerTest.generateDTOList();
            response.setContent(memberResultList);

            Mockito.when(memberService.getAllMembers(0)).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.MEMBERS).param("page", "0"))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).getAllMembers(0);
        }

    }

    @Nested
    class postMember {

        @Test
        @DisplayName("Valid Case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test_1() throws Exception {

            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);

            Mockito.when(memberService.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.MEMBERS)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(response)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).create(Mockito.any());
        }

        @Test
        @DisplayName("No Token Provided")
        void test_2() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            Mockito.when(memberService.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.MEMBERS)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).create(Mockito.any());

        }

        @Test
        @DisplayName("Token not valid")
        void test_3 () throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            Mockito.when(memberService.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.MEMBERS)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).create(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.MemberControllerTest#generateRequestWithMissingMandatoryAttribute")
        @DisplayName("Mandatory attr missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test_4 (MemberDTORequest requestMissingAttr) throws Exception {
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);
            Mockito.when(memberService.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.MEMBERS)
                    .content(jsonMapper.writeValueAsString(requestMissingAttr))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).create(Mockito.any());
        }

        @Test
        @DisplayName("Invalid request name format")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test_5 () throws Exception {

            MemberDTORequest request = MemberControllerTest.generateDTOListRequest().get(0);
            request.setName("1234567890");

            Mockito.when(memberService.create(Mockito.any())).thenThrow(new RuntimeException("Formato de Nombre invalido"));

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.MEMBERS)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).create(Mockito.any());
        }
    }

    @Nested
    class deleteMember {

        @Test
        @DisplayName("Valid Case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_1 () throws Exception {
            String idParam = "?id=123";
            String result = "Successfully deleted member with id 123";

            Mockito.when(memberService.deleteMember(Mockito.any())).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.MEMBERS + idParam))
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).deleteMember(Mockito.any());
        }

        @Test
        @DisplayName("No Token provided")
        void test_2 () throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            String idParam = "?id=123";
            String result = "Successfully deleted member with id 123";

            Mockito.when(memberService.deleteMember(Mockito.any())).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.MEMBERS + idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).deleteMember(Mockito.any());
        }

        @Test
        @DisplayName("Invalid Token")
        void test_3 () throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            String idParam = "?id=123";
            String result = "Successfully deleted member with id 123";

            Mockito.when(memberService.deleteMember(Mockito.any())).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.MEMBERS + idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).deleteMember(Mockito.any());

        }

        @Test
        @DisplayName("No idParam provided")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_4 () throws Exception {
            String result = "Successfully deleted member with id";

            Mockito.when(memberService.deleteMember(Mockito.any())).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.MEMBERS))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).deleteMember(Mockito.any());
        }

        @Test
        @DisplayName("Id not found")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_5 () throws Exception {

            String idParam = "?id=123";

            Mockito.when(memberService.deleteMember(Mockito.any())).thenThrow(new NotFoundException("Error: Member wih id: " +idParam+ " was not found"));

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.MEMBERS + idParam))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).deleteMember(Mockito.any());
        }

        @Test
        @DisplayName("Invalid user role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test_6 () throws Exception {

            String idParam = "?id=123";
            String result = "Successfully deleted member with id 123";

            Mockito.when(memberService.deleteMember(Mockito.any())).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.MEMBERS + idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).deleteMember(Mockito.any());
        }

    }

    @Nested
    class editMember{

        @Test
        @DisplayName("Valid Case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_1 () throws Exception {

            String id = "123";
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);

            Mockito.when(memberService.edit(Mockito.any(), Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.MEMBERS + "/{id}", id)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(response)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("No Token provided")
        void test_2 () throws Exception {

            String id = "123";
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            Mockito.when(memberService.edit(Mockito.any(), Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.MEMBERS + "/{id}", id)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Invalid Token")
        void test_3 () throws Exception {

            String id = "123";
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            Mockito.when(memberService.edit(Mockito.any(), Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.MEMBERS + "/{id}", id)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Invalid Member ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_4 () throws Exception {

            String id = "123";
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);

            Mockito.when(memberService.edit(Mockito.any(), Mockito.any())).thenThrow(new MemberNotFoundException("Member with the provided ID was not found over the system"));

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.MEMBERS + "/{id}", id)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Member name already present")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_5 () throws Exception {

            String id = "123";
            MemberDTORequest request =  MemberControllerTest.generateDTOListRequest().get(0);
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);

            Mockito.when(memberService.edit(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Name format invalid"));

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.MEMBERS + "/{id}", id)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService).edit(Mockito.any(), Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.MemberControllerTest#generateRequestWithMissingMandatoryAttribute")
        @DisplayName("Mandatory attr missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test_6 (MemberDTORequest request) throws Exception {

            String id = "123";
            MemberDTOResponse response = MemberControllerTest.generateDTOList().get(0);

            Mockito.when(memberService.edit(Mockito.any(), Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.MEMBERS + "/{id}", id)
                    .content(jsonMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TestName"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(memberService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }
    }
}
