package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.OrganizationDTORequest;
import com.alkemy.ong.dto.OrganizationInfoResponse;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.*;
import com.alkemy.ong.utility.GlobalConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebMvcTest(OrganizationController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    JwtService jwtService; // TO MOCK CREDENTIALS
    @MockBean
    UserDetailsServiceImpl userDetailsService; // NOT USED. ADDED JUST TO DEAL WITH SECURITY DEPENDENCY LOADING ISSUES.
    @MockBean
    private OrganizationService organizationService;
    @MockBean
    private SlidesService slidesService;

    ObjectMapper jsonMapper = new ObjectMapper();

    @Nested
    class getAllTest {

        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test1() throws Exception {

            // VALID SERVICE RESULT MOCK
            List<ReducedOrganizationDTO> mockResultList = OrganizationControllerTest.generateMockReducedOrgDtoList();
            Mockito.when(organizationService.getAll()).thenReturn(mockResultList);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(mockResultList)))
                    .andDo(MockMvcResultHandlers.print());

        }

        @Test
        @DisplayName("No token provided")
        void test2() throws Exception {

            // CREDENTIALS MOCK: TOKEN NOT SENT
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false); // Simulate: token was not provided.

            // VALID SERVICE RESULT MOCK
            List<ReducedOrganizationDTO> mockResultList = OrganizationControllerTest.generateMockReducedOrgDtoList();
            Mockito.when(organizationService.getAll()).thenReturn(mockResultList);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getAll(); // Verify that the service method was never called.

        }

        @Test
        @DisplayName("Token not valid")
        void test3() throws Exception {

            // CREDENTIALS MOCK: TOKEN SENT BUT NOT VALID
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true); // Simulate: token was sent
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception()); // Simulate: token verification failed.

            // VALID SERVICE RESULT MOCK
            List<ReducedOrganizationDTO> mockResultList = OrganizationControllerTest.generateMockReducedOrgDtoList();
            Mockito.when(organizationService.getAll()).thenReturn(mockResultList);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getAll(); // Verify that the service method was never called.

        }

    }

    @Nested
    class getByIdTest {

        @Test
        @DisplayName("Valid case")
        void test1()  {

        }

        @Test
        @DisplayName("No token provided")
        void test2() {

        }

        @Test
        @DisplayName("Token not valid")
        void test3() {

        }

        @Test
        @DisplayName("Valid token but no role admin")
        void test4() {

        }

        @Test
        @DisplayName("Non-existing ID")
        void test5() {

        }

    }

    @Nested
    class updateOrganizationTest {

        @Test
        @DisplayName("Valid case")
        void test1() {

        }

        @Test
        @DisplayName("No token provided")
        void test2() {

        }

        @Test
        @DisplayName("Token not valid")
        void test3() {

        }

        @Test
        @DisplayName("Valid token but no role admin")
        void test4() {

        }

        @Test
        @DisplayName("Non-existing ID")
        void test5() {

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.OrganizationControllerTest#generateRequestsWithMissingMandatoryAttributes")
        @DisplayName("Mandatory attributes missing")
        void test6(OrganizationDTORequest requestWithMissingAttribute) {

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.OrganizationControllerTest#generateRequestsWithBrokenAttributes")
        @DisplayName("Invalid attribute format")
        void test7(OrganizationDTORequest requestWithBrokenAttribute) {

        }

    }

    static List<OrganizationDTORequest> generateRequestsWithMissingMandatoryAttributes() {
        return Collections.singletonList(new OrganizationDTORequest());
    }

    static List<OrganizationDTORequest> generateRequestsWithBrokenAttributes() {
        return Collections.singletonList(new OrganizationDTORequest());
    }

    static List<ReducedOrganizationDTO> generateMockReducedOrgDtoList() {
        ReducedOrganizationDTO dto = new ReducedOrganizationDTO();
        dto.setName("Somos Más");
        dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1658005269829-fundacion-mas.png");
        dto.setPhone(Integer.parseInt("1160112988"));
        dto.setAddress("La Cava, Cordoba, Argentina");
        dto.setUrlFacebook("facebook.com/somosmas");
        dto.setUrlInstagram("instagram.com/somosmas");
        dto.setUrlLinkedin("linkedin.com/somosmas");
        return Collections.singletonList(dto);
    }

}
