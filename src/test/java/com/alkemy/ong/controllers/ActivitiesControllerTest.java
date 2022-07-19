package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.dto.ActivityDTORequest;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.ActivityNotFoundException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.ActivitiesService;
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
import java.util.List;


@WebMvcTest(ActivitiesController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class ActivitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserDetailsServiceImpl userDetailsService; // NOT USED. ADDED JUST TO DEAL WITH SECURITY DEPENDENCY LOADING ISSUES.

    @MockBean
    JwtService jwtService; // TO MOCK CREDENTIALS

    @MockBean
    private ActivitiesService activitiesService;

    ObjectMapper jsonMapper = new ObjectMapper();


    @Nested
    class saveActivityTest{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception{
            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();

            Mockito.when(activitiesService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES)
                        .content(jsonMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService).save(Mockito.any());
        }

        @Test
        @DisplayName("Name already exists")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws Exception{
            ActivityDTORequest request = generateActivityRequest();

            Mockito.when(activitiesService.save(Mockito.any())).thenThrow(new ActivityNamePresentException());

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService).save(Mockito.any());
        }

        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test3() throws  Exception{
            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();

            Mockito.when(activitiesService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void test4() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();

            Mockito.when(activitiesService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES )
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void test5() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();

            Mockito.when(activitiesService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).save(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.ActivitiesControllerTest#generateRequestMissingMandatoryAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test6(ActivityDTORequest requestWithMissingAttribute) throws Exception {
            ActivityDTO expectedResponse = generateActivityDTO();
            Mockito.when(activitiesService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES)
                            .content(jsonMapper.writeValueAsString(requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).save(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.ActivitiesControllerTest#generateRequestWithBrokenAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test7(ActivityDTORequest requestWithBrokenAttribute) throws Exception{
            ActivityDTO expectedResponse = generateActivityDTO();
            Mockito.when(activitiesService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.ACTIVITIES)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).save(Mockito.any());
        }
    }

    @Nested
    class updateActivityTest{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception {
            String id = "id123";
            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();

            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws Exception {
            String id = "id123";
            ActivityDTORequest request = generateActivityRequest();

            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenThrow(new ActivityNotFoundException());

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Name already exists")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test3() throws Exception{
            String id = "id123";
            ActivityDTORequest request = generateActivityRequest();

            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenThrow(new ActivityNamePresentException());

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws  Exception{
            String id = "id123";
            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();

            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }


        @Test
        @DisplayName("Token not valid")
        void test5() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();
            String id = "id123";

            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                        .content(jsonMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void test6() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            ActivityDTORequest request = generateActivityRequest();
            ActivityDTO expectedResponse = generateActivityDTO();
            String id = "id123";

            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.ActivitiesControllerTest#generateRequestMissingMandatoryAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test7(ActivityDTORequest requestWithMissingAttribute) throws Exception {
            ActivityDTO expectedResponse = generateActivityDTO();
            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);
            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                        .content(jsonMapper.writeValueAsString(requestWithMissingAttribute))
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.ActivitiesControllerTest#generateRequestWithBrokenAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test8(ActivityDTORequest requestWithBrokenAttribute) throws Exception{
            ActivityDTO expectedResponse = generateActivityDTO();
            Mockito.when(activitiesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);
            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.ACTIVITIES + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(activitiesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }
    }

    private static ActivityDTO generateActivityDTO(){
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setName("Activity Test DTO");
        activityDTO.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657325315906-tutorias.jpg");
        activityDTO.setContent("Es un programa destinado a jovenes a partir del tercer año de secundaria,\n" +
                " cuyo objetivo es garantizar... etc");

        return activityDTO;
    }

    private static ActivityDTORequest generateActivityRequest(){
        ActivityDTORequest request = new ActivityDTORequest();
        request.setName("Activity Test request");
        request.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657325315906-tutorias.jpg");
        request.setContent("Es un programa destinado a jovenes a partir del tercer año de secundaria,\n" +
                " cuyo objetivo es garantizar... etc");
        request.setEncoded_image(
                new EncodedImageDTO(
                        "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                        "sample.png"
                )
        );

        return request;
    }

    private static List<ActivityDTORequest> generateRequestMissingMandatoryAttributes(){
        List<ActivityDTORequest> requests = new ArrayList<>();
        ActivityDTORequest singleRequest;

        // CASE 1: Missing name
        singleRequest = generateActivityRequest();
        singleRequest.setName(null);
        requests.add(singleRequest);

        // CASE 2: Missing content
        singleRequest = generateActivityRequest();
        singleRequest.setContent(null);
        requests.add(singleRequest);

        // CASE 3: Missing encoded file
        singleRequest = generateActivityRequest();
        singleRequest.setEncoded_image(new EncodedImageDTO("sample.png", null));
        requests.add(singleRequest);

        // CASE 4: Missing file name
        singleRequest = generateActivityRequest();
        singleRequest.setEncoded_image(new EncodedImageDTO(null, "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB"));
        requests.add(singleRequest);

        return requests;
    }

    private static List<ActivityDTORequest> generateRequestWithBrokenAttributes(){
        List<ActivityDTORequest> requests = new ArrayList<>();
        ActivityDTORequest singleRequest;

        // CASE 1: Blank name
        singleRequest = generateActivityRequest();
        singleRequest.setName("");
        requests.add(singleRequest);

        // CASE 2: Blank content
        singleRequest = generateActivityRequest();
        singleRequest.setContent("");
        requests.add(singleRequest);

        // CASE 3: Blank encoded file
        singleRequest = generateActivityRequest();
        singleRequest.setEncoded_image(new EncodedImageDTO("sample.png", ""));
        requests.add(singleRequest);

        // CASE 4: Blank file name
        singleRequest = generateActivityRequest();
        singleRequest.setEncoded_image(new EncodedImageDTO("", "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB"));
        requests.add(singleRequest);

        return requests;
    }
}
