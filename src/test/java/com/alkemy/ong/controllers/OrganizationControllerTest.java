package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.*;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.*;
import com.alkemy.ong.utility.GlobalConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
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

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService).getAll(); // Verify that the service method was called.

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
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getAll(); // Verify that the service method was NOT called.

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
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getAll(); // Verify that the service method was NOT called.

        }

    }

    @Nested
    class getByIdTest {

        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception {

            // VALID SERVICE RESULT MOCK
            String requestOrganizationId = "orgId";
            ReducedOrganizationDTO orgDto = OrganizationControllerTest.generateMockReducedOrgDtoList().get(0);
            Mockito.when(organizationService.getById(requestOrganizationId)).thenReturn(orgDto);
            List<SlidesEntityDTO> orgSlides = OrganizationControllerTest.generateMockOrgSlides();
            Mockito.when(slidesService.findByOrganization(requestOrganizationId)).thenReturn(orgSlides);
            OrganizationInfoResponse expectendResponseBody = new OrganizationInfoResponse(orgDto, orgSlides);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO + "/{id}", requestOrganizationId))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectendResponseBody)))
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService).getById(requestOrganizationId); // Verify that the service method was called.
            Mockito.verify(slidesService).findByOrganization(requestOrganizationId); // Verify that the service method was called.

        }

        @Test
        @DisplayName("No token provided")
        void test2() throws Exception {

            // CREDENTIALS MOCK: TOKEN NOT SENT
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false); // Simulate: token was not provided.

            // VALID SERVICE RESULT MOCK
            String requestOrganizationId = "orgId";
            ReducedOrganizationDTO orgDto = OrganizationControllerTest.generateMockReducedOrgDtoList().get(0);
            Mockito.when(organizationService.getById(requestOrganizationId)).thenReturn(orgDto);
            List<SlidesEntityDTO> orgSlides = OrganizationControllerTest.generateMockOrgSlides();
            Mockito.when(slidesService.findByOrganization(requestOrganizationId)).thenReturn(orgSlides);
            OrganizationInfoResponse expectendResponseBody = new OrganizationInfoResponse(orgDto, orgSlides);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO + "/{id}", requestOrganizationId))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("organization")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getById(Mockito.any()); // Verify that the service method was NOT called.
            Mockito.verify(slidesService, Mockito.never()).findByOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @Test
        @DisplayName("Token not valid")
        void test3() throws Exception {

            // CREDENTIALS MOCK: TOKEN SENT BUT NOT VALID
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true); // Simulate: token was sent
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception()); // Simulate: token verification failed.

            // VALID SERVICE RESULT MOCK
            String requestOrganizationId = "orgId";
            ReducedOrganizationDTO orgDto = OrganizationControllerTest.generateMockReducedOrgDtoList().get(0);
            Mockito.when(organizationService.getById(requestOrganizationId)).thenReturn(orgDto);
            List<SlidesEntityDTO> orgSlides = OrganizationControllerTest.generateMockOrgSlides();
            Mockito.when(slidesService.findByOrganization(requestOrganizationId)).thenReturn(orgSlides);
            OrganizationInfoResponse expectendResponseBody = new OrganizationInfoResponse(orgDto, orgSlides);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO + "/{id}", requestOrganizationId))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("organization")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());


            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getById(Mockito.any()); // Verify that the service method was NOT called.
            Mockito.verify(slidesService, Mockito.never()).findByOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @Test
        @DisplayName("Valid authentication but role is NOT admin")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws Exception {

            // VALID SERVICE RESULT MOCK
            String requestOrganizationId = "orgId";
            ReducedOrganizationDTO orgDto = OrganizationControllerTest.generateMockReducedOrgDtoList().get(0);
            Mockito.when(organizationService.getById(requestOrganizationId)).thenReturn(orgDto);
            List<SlidesEntityDTO> orgSlides = OrganizationControllerTest.generateMockOrgSlides();
            Mockito.when(slidesService.findByOrganization(requestOrganizationId)).thenReturn(orgSlides);
            OrganizationInfoResponse expectendResponseBody = new OrganizationInfoResponse(orgDto, orgSlides);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO + "/{id}", requestOrganizationId))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("organization")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());


            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).getById(Mockito.any()); // Verify that the service method was NOT called.
            Mockito.verify(slidesService, Mockito.never()).findByOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test5() throws Exception {

            // SERVICE RESULT MOCK
            String requestOrganizationId = "orgId";
            Mockito.when(organizationService.getById(requestOrganizationId)).thenThrow(new RuntimeException());
            Mockito.when(slidesService.findByOrganization(requestOrganizationId)).thenReturn(Collections.emptyList());

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO + "/{id}", requestOrganizationId))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());


            // OTHER VERIFICATIONS
            Mockito.verify(organizationService).getById(requestOrganizationId); // Verify that the organization service method was called.
            Mockito.verify(slidesService, Mockito.never()).findByOrganization(Mockito.any()); // Verify that the slides service method was NOT called.

        }

    }

    @Nested
    class updateOrganizationTest {

        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception {

            // VALID SERVICE RESULT MOCK
            OrganizationDTORequest orgDto = OrganizationControllerTest.generateMockOrgDtoRequest();
            OrganizationDTO expectedResponse = OrganizationControllerTest.generateMockOrgDto();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenReturn(expectedResponse);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(orgDto))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService).updateOrganization(Mockito.any()); // Verify that the service method was called.

        }

        @Test
        @DisplayName("No token provided")
        void test2() throws Exception {

            // CREDENTIALS MOCK: TOKEN NOT SENT
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false); // Simulate: token was not provided.

            // VALID SERVICE RESULT MOCK
            OrganizationDTORequest orgDto = OrganizationControllerTest.generateMockOrgDtoRequest();
            OrganizationDTO expectedResponse = OrganizationControllerTest.generateMockOrgDto();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenReturn(expectedResponse);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(orgDto))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).updateOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @Test
        @DisplayName("Token not valid")
        void test3() throws Exception {

            // CREDENTIALS MOCK: TOKEN SENT BUT NOT VALID
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true); // Simulate: token was sent
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception()); // Simulate: token verification failed.

            // VALID SERVICE RESULT MOCK
            OrganizationDTORequest orgDto = OrganizationControllerTest.generateMockOrgDtoRequest();
            OrganizationDTO expectedResponse = OrganizationControllerTest.generateMockOrgDto();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenReturn(expectedResponse);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(orgDto))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).updateOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @Test
        @DisplayName("Valid token but role is NOT admin")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws Exception {

            // VALID SERVICE RESULT MOCK
            OrganizationDTORequest orgDto = OrganizationControllerTest.generateMockOrgDtoRequest();
            OrganizationDTO expectedResponse = OrganizationControllerTest.generateMockOrgDto();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenReturn(expectedResponse);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(orgDto))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).updateOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test5() throws Exception {

            // VALID SERVICE RESULT MOCK
            OrganizationDTORequest orgDto = OrganizationControllerTest.generateMockOrgDtoRequest();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenThrow(new RuntimeException());

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(orgDto))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService).updateOrganization(Mockito.any()); // Verify that the service method was called.

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.OrganizationControllerTest#generateRequestsWithMissingMandatoryAttributes")
        @DisplayName("Mandatory attributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test6(OrganizationDTORequest requestWithMissingAttribute) throws Exception {

            // VALID SERVICE RESULT MOCK
            OrganizationDTO expectedResponse = OrganizationControllerTest.generateMockOrgDto();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenReturn(expectedResponse);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).updateOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.OrganizationControllerTest#generateRequestsWithBrokenAttributes")
        @DisplayName("Invalid attribute format")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test7(OrganizationDTORequest requestWithBrokenAttribute) throws Exception {

            // VALID SERVICE RESULT MOCK
            OrganizationDTO expectedResponse = OrganizationControllerTest.generateMockOrgDto();
            Mockito.when(organizationService.updateOrganization(Mockito.any())).thenReturn(expectedResponse);

            // REQUEST ASSESSMENT
            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos")))) // Verify that the response body is not the valid one.
                    .andDo(MockMvcResultHandlers.print());

            // OTHER VERIFICATIONS
            Mockito.verify(organizationService, Mockito.never()).updateOrganization(Mockito.any()); // Verify that the service method was NOT called.

        }

    }

    static List<OrganizationDTORequest> generateRequestsWithMissingMandatoryAttributes() {
        List<OrganizationDTORequest> requestList = new ArrayList<>();
        OrganizationDTORequest dto;
        // CASE 1: MISSING ID
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setId(null);
        requestList.add(dto);
        // CASE 2: MISSING NAME
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setName(null);
        requestList.add(dto);
        // CASE 3: MISSING IMAGE URL
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setImage(null);
        requestList.add(dto);
        // CASE 4: MISSING EMAIL
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEmail(null);
        requestList.add(dto);
        // CASE 5: MISSING WELCOME TEXT
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setWelcomeText(null);
        requestList.add(dto);
        // CASE 6: IMAGE FILE PRESENT, BUT MISSING ENCODED IMAGE FIELD.
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEncoded_image(new EncodedImageDTO(null, "sample.png"));
        requestList.add(dto);
        // CASE 7: IMAGE FILE PRESENT, BUT MISSING FILE NAME FIELD.
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEncoded_image(new EncodedImageDTO("iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=", null));
        requestList.add(dto);

        return requestList;
    }

    static List<OrganizationDTORequest> generateRequestsWithBrokenAttributes() {
        List<OrganizationDTORequest> requestList = new ArrayList<>();
        OrganizationDTORequest dto;
        // CASE 1: BLANK ID
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setId("");
        requestList.add(dto);
        // CASE 2: BLANK NAME
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setName("");
        requestList.add(dto);
        // CASE 3: BLANK IMAGE URL
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setImage("");
        requestList.add(dto);
        // CASE 4: BLANK EMAIL
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEmail("");
        requestList.add(dto);
        // CASE 5: INVALID EMAIL FORMAT
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEmail("this is not an email");
        requestList.add(dto);
        // CASE 6: BLANK WELCOME TEXT
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setWelcomeText("");
        requestList.add(dto);
        // CASE 7: IMAGE FILE PRESENT, BUT BLANK ENCODED IMAGE FIELD.
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEncoded_image(new EncodedImageDTO("", "sample.png"));
        requestList.add(dto);
        // CASE 8: IMAGE FILE PRESENT, BUT BLANK FILE NAME FIELD.
        dto = OrganizationControllerTest.generateMockOrgDtoRequest();
        dto.setEncoded_image(new EncodedImageDTO("iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=", ""));
        requestList.add(dto);

        return requestList;
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

    static List<SlidesEntityDTO> generateMockOrgSlides() {
        return Arrays.asList(
                new SlidesEntityDTO()
                        .setId("asd1")
                        .setOrganizationId("orgId")
                        .setImageUrl("slide1 imageurl")
                        .setText("slide 1")
                        .setSlideOrder(1),
                new SlidesEntityDTO()
                        .setId("asd2")
                        .setOrganizationId("orgId")
                        .setImageUrl("slide2 imageurl")
                        .setText("slide 2")
                        .setSlideOrder(2)
        );
    }

    static OrganizationDTORequest generateMockOrgDtoRequest() {
        OrganizationDTORequest dto = new OrganizationDTORequest();
        dto.setId("orgId");
        dto.setName("Somos Más");
        dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1658005269829-fundacion-mas.png");
        dto.setPhone(Integer.parseInt("1160112988"));
        dto.setAddress("La Cava, Cordoba, Argentina");
        dto.setUrlFacebook("facebook.com/somosmas");
        dto.setUrlInstagram("instagram.com/somosmas");
        dto.setUrlLinkedin("linkedin.com/somosmas");
        dto.setEmail("somos.mas@mockgmail.com");
        dto.setWelcomeText("Mock welcome text");
        dto.setAboutUsText("Mock about us text");
        dto.setEncoded_image(
                new EncodedImageDTO(
                        "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                        "sample.png"
                )
        );
        return dto;
    }

    static OrganizationDTO generateMockOrgDto() {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId("orgId");
        dto.setName("Somos Más");
        dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1658005269829-fundacion-mas.png");
        dto.setPhone(Integer.parseInt("1160112988"));
        dto.setAddress("La Cava, Cordoba, Argentina");
        dto.setUrlFacebook("facebook.com/somosmas");
        dto.setUrlInstagram("instagram.com/somosmas");
        dto.setUrlLinkedin("linkedin.com/somosmas");
        dto.setEmail("somos.mas@mockgmail.com");
        dto.setWelcomeText("Mock welcome text");
        dto.setAboutUsText("Mock about us text");
        return dto;
    }

}
