package com.alkemy.ong.controllers;
import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.*;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.ActivitiesService;
import com.alkemy.ong.services.impl.AmazonS3ServiceImpl;
import com.alkemy.ong.services.impl.NewsServiceImpl;
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

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@WebMvcTest(NewsController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtService jwtService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    AmazonS3ServiceImpl amazonS3Service;
    @MockBean
    private NewsServiceImpl newsService;

    String url = GlobalConstants.Endpoints.NEWS;

    String page = GlobalConstants.PAGE_INDEX_PARAM;
    ObjectMapper jsonMapper = new ObjectMapper();

    @Nested
    class testGetByIdNew {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetNew() throws Exception {
            NewsDTORequest request = createMockNewsRequest();
            String id = request.getId();
            Mockito.when(newsService.findById(id)).thenReturn(request);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(request)))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService).findById(id);
        }

        @Test
        @DisplayName("Token not provided")
        void testGetNewWithoutToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);
            NewsDTORequest request = createMockNewsRequest();
            String id = request.getId();
            Mockito.when(newsService.findById(id)).thenReturn(request);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("novedad"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).findById(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void testNewWithInvalidToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            NewsDTORequest request = createMockNewsRequest();
            String id = request.getId();
            Mockito.when(newsService.findById(id)).thenReturn(request);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("novedad"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).findById(Mockito.any());
        }

        @Test
        @DisplayName("Valid authentication but role is NOT admin")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testGetNewWithRoleUser() throws Exception {

            NewsDTORequest request = createMockNewsRequest();
            String id = request.getId();
            Mockito.when(newsService.findById(id)).thenReturn(request);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("novedad"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).findById(Mockito.any());
        }


        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetNewWithNonExistingId() throws Exception {

            String requestId = "id123";
            Mockito.when(newsService.findById(requestId)).thenThrow(new RuntimeException());

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", requestId))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("novedad"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService).findById(requestId);
        }
    }
    @Nested
    class testGetAllNews {

        @DisplayName("Valid Case")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.NewsControllerTest#validRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetAllNewsWithValidTokenAndPage(int pageNumber) throws Exception {
            //test with valid token
            PageResultResponse<DatedNewsDTO> mockNewPageResultList = generateMockNewsList(pageNumber);
            PageResultResponse<DatedNewsDTO> mockCategoryPageResultListValid = generateMockNewsList(0);
            Mockito.when(newsService.getAllNews(pageNumber)).thenReturn(mockNewPageResultList);
            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(mockCategoryPageResultListValid)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService).getAllNews(pageNumber);

        }

        @Test
        @DisplayName("PageIndexOutOfBoundsException")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetAllNewsWithValidTokenAndInvalidPage() throws Exception {
            //test with valid token
            int pageNumber = -14;
            Mockito.when(newsService.getAllNews(pageNumber)).thenThrow(new PageIndexOutOfBoundsException());

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService).getAllNews(pageNumber);

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTest#validRange")
        @DisplayName("No token provided")
        void testGetAllNewsWithoutToken(int pageNumber) throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            PageResultResponse<DatedNewsDTO> mockNewPageResultList = generateMockNewsList(pageNumber);
            Mockito.when(newsService.getAllNews(pageNumber)).thenReturn(mockNewPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).getAllNews(pageNumber);

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.NewsControllerTest#validRange")
        @DisplayName("Token not valid")
        void testGetAllNewsWithInvalidToken(int pageNumber) throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);

            PageResultResponse<DatedNewsDTO> mockNewPageResultList = generateMockNewsList(pageNumber);
            Mockito.when(newsService.getAllNews(pageNumber)).thenReturn(mockNewPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).getAllNews(pageNumber);

        }

        @DisplayName("Valid authentication but role is NOT admin")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.NewsControllerTest#validRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testAllNewsWithTokenRoleUser(int pageNumber) throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);

            PageResultResponse<DatedNewsDTO> mockNewPageResultList = generateMockNewsList(pageNumber);
            Mockito.when(newsService.getAllNews(pageNumber)).thenReturn(mockNewPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).getAllNews(pageNumber);

        }

    }

    @Nested
    class testUpdateNew {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateNewsCorrect() throws Exception {
            String id = "id123";
            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService).updateNews(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateNewsWithNonExistingId() throws Exception {
            String id = "id123";

            NewsDTORequest request = createMockNewsRequest();
            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenThrow(new EntityNotFoundException());

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService).updateNews(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testUpdateNewsWithInvalidRole() throws Exception {
            String id = "id123";
            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(expectedResponse))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News TestDTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).updateNews(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void testUpdateNewsWithInvalidToken() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            String id = "id123";
            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).updateNews(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void testUpdateNewsWithoutToken() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);


            String id = "id123";
            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).updateNews(Mockito.any(), Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.NewsControllerTest#generateRequestsWithMissingAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateNewsWithMissingAttributes(NewsDTORequest requestWithMissingAttribute) throws Exception {
            NewsDTO expectedResponse = createMockNewsDTO();
            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);
            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).updateNews(Mockito.any(), Mockito.any());
        }
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.NewsControllerTest#generateRequestsWithBrokenAttribute")
        @DisplayName("Broken Attributes")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateNewsWithBrokenAttributes(NewsDTORequest requestWithBrokenAttribute) throws Exception {
            NewsDTO expectedResponse = createMockNewsDTO();
            Mockito.when(newsService.updateNews(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);
            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).updateNews(Mockito.any(), Mockito.any());
        }


    }

    @Nested
    class testDeleteNew {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testDeleteNewWithValidToken() throws Exception {

            String id = "id123";
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService).deleteNews(id);
        }

        @Test
        @DisplayName("Token not valid")
        void testDeleteNewWithInvalidToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());
            String id = "id123";
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).deleteNews(Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void testDeleteNewWithoutToken() throws Exception {

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false); // Simulate: token was not provided.

            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).deleteNews(Mockito.any());
        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testDeleteNewWithNonExistingId() throws Exception {
            String id = "id123";
            Mockito.when(newsService.deleteNews(id)).thenThrow(new EntityNotFoundException());
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService).deleteNews(id);


        }

        @Test
        @DisplayName("Valid authentication but role is NOT admin")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testDeleteNewWithIncorrectUser() throws Exception {

            String id = "id123";
            Mockito.doThrow(new RuntimeException()).when(newsService).deleteNews(id);
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(newsService, Mockito.never()).deleteNews(Mockito.any());
        }
    }

    @Nested
    class testSaveNew {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception{
            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService).save(Mockito.any());
        }

        /*@Test
        @DisplayName("Name already exists")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws Exception{
            NewsDTORequest request = createMockNewsRequest();

            Mockito.when(newsService.save(Mockito.any())).thenThrow(new RuntimeException());

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isConflict())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService).save(Mockito.any());
        }*/

        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test3() throws  Exception{
            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void test4() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(url )
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void test5() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            NewsDTORequest request = createMockNewsRequest();
            NewsDTO expectedResponse = createMockNewsDTO();

            Mockito.when(newsService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).save(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.NewsControllerTest#generateRequestsWithMissingAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test6(NewsDTORequest requestWithMissingAttribute) throws Exception {
            NewsDTO expectedResponse = createMockNewsDTO();
            Mockito.when(newsService.save(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("News Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(newsService, Mockito.never()).save(Mockito.any());
        }

    }

        static NewsDTO createMockNewsDTO() {
            NewsDTO dto = new NewsDTO();
            CategoryDTO cat = new CategoryDTO();
            cat.setName("Salud");
            dto.setId("id123");
            dto.setName("News Test DTO");
            dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1658005269841-novedad1.png");
            dto.setContent("Esta novedad es acerca de la pandemia");
            dto.setCategory(cat);
            return dto;
        }

        static NewsDTORequest createMockNewsRequest() {
            CategoryDTO cat = new CategoryDTO();
            cat.setName("Salud");
            NewsDTORequest request = new NewsDTORequest();
            request.setId("id123");
            request.setName("News Test Request");
            request.setContent("Noticia que trata la situacion de la pandemia que hubo durante 2 años");
            request.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1658005269841-novedad1.png");
            request.setCategory(cat);
            request.setEncoded_image(new EncodedImageDTO("iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                    "sample.png"));
            return request;
        }

        static List<NewsDTORequest> generateRequestsWithMissingAttributes() {
            List<NewsDTORequest> requestList = new ArrayList<>();
            NewsDTORequest dto;

            //missing name
            dto = createMockNewsRequest();
            dto.setName(null);
            requestList.add(dto);

            //missing content
            dto = createMockNewsRequest();
            dto.setContent(null);
            requestList.add(dto);

            //missing content
            dto = createMockNewsRequest();
            dto.setCategory(null);
            requestList.add(dto);

            return requestList;
        }

        static List<NewsDTORequest> generateRequestsWithBrokenAttribute() {
            List<NewsDTORequest> requestList = new ArrayList<>();
            CategoryDTO cat = new CategoryDTO();
            cat.setName("21&$#");
            NewsDTORequest dto;

            //case 1: blank name
            dto = createMockNewsRequest();
            dto.setName("");
            dto.setCategory(cat);
            requestList.add(dto);

            //case 2: blank content
            dto = createMockNewsRequest();
            dto.setContent("");
            dto.setCategory(cat);
            requestList.add(dto);

            //case 3: Blank encoded file
            dto = createMockNewsRequest();
            dto.setCategory(cat);
            dto.setEncoded_image(new EncodedImageDTO("sample.png", ""));
            requestList.add(dto);

            dto = createMockNewsRequest();
            dto.setCategory(cat);
            dto.setEncoded_image(new EncodedImageDTO(
        "", "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB"));;
            requestList.add(dto);

            return requestList;
        }

        static PageResultResponse<DatedNewsDTO> generateMockNewsList(int pageNumber) {
            int cantPaginas = 10;
            int tamPagina = GlobalConstants.GLOBAL_PAGE_SIZE;
            List<DatedNewsDTO> list = new LinkedList<>();

            if (pageNumber < 0 || pageNumber > cantPaginas - 1) {
                list = new LinkedList<>();
            } else {
                for (int i = 0; i < tamPagina; i++) {
                    DatedNewsDTO datedNewsDTO = new DatedNewsDTO();
                    datedNewsDTO.setName("Novedad Mockito "+i);
                    list.add(datedNewsDTO);
                }
            }
            return new PageResultResponse<DatedNewsDTO>()
                    .setContent(list)
                    .setNext_page_url("Next Page")
                    .setPrevious_page_url("Previous Page");
        }

        static Stream<Integer> validRange() {
            return IntStream.range(0, 10).boxed();
        }

        static Stream<Integer> invalidRange() {
            return IntStream.range(10, 20).boxed();
        }
    }
