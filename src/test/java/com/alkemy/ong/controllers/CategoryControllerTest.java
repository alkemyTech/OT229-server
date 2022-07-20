package com.alkemy.ong.controllers;
import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.*;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.CategoriesService;
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

@WebMvcTest(CategoriesController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtService jwtService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    private CategoriesService categoriesService;
    String url = GlobalConstants.Endpoints.CATEGORIES;

    String page = GlobalConstants.PAGE_INDEX_PARAM;
    ObjectMapper jsonMapper = new ObjectMapper();

    @Nested
    class testGetByIdCategory{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetCategory() throws Exception {
            String requestId = "id123";
            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();
            categoryDTO.setId(requestId);
            Mockito.when(categoriesService.getById(requestId)).thenReturn(categoryDTO);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", requestId))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(categoryDTO)))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService).getById(requestId);
        }
        @Test
        @DisplayName("Token not provided")
        void testGetCategoryWitoutToken() throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);
            String requestId = "id123";
            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();
            categoryDTO.setId(requestId);
            Mockito.when(categoriesService.getById(requestId)).thenReturn(categoryDTO);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", requestId))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("categoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService,Mockito.never()).getById(Mockito.any());
        }
        @Test
        @DisplayName("Token not valid")
        void testGetCategoryWithInvalidToken() throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            String requestId = "id123";
            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();
            categoryDTO.setId(requestId);
            Mockito.when(categoriesService.getById(requestId)).thenReturn(categoryDTO);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", requestId))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("categoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService,Mockito.never()).getById(Mockito.any());
        }
        @Test
        @DisplayName("Valid authentication but role is NOT admin")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testGetCategoryWithRoleUser() throws Exception{

            String requestId = "id123";
            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();
            categoryDTO.setId(requestId);
            Mockito.when(categoriesService.getById(requestId)).thenReturn(categoryDTO);

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", requestId))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("categoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService,Mockito.never()).getById(Mockito.any());
        }
        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetCategoryWithNonExistingId() throws Exception{

            String requestId = "id123";
            Mockito.when(categoriesService.getById(requestId)).thenThrow(new RuntimeException());

            mockMvc.perform(MockMvcRequestBuilders.get(url + "/{id}", requestId))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("categoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService).getById(requestId);
        }

    }
    @Nested
    class testGetAllCategories{

        @DisplayName("Valid Case")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#validRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetAllCategoriesWithValidTokenAndPage(int pageNumber) throws Exception{
            //test with valid token
            PageResultResponse<String> mockCategoryPageResultList = CategoryControllerTest.generateMockCategoryList(pageNumber);
            PageResultResponse<String> mockCategoryPageResultListValid = CategoryControllerTest.generateMockCategoryList(0);
            Mockito.when(categoriesService.getAllCategoryNames(pageNumber)).thenReturn(mockCategoryPageResultList);
            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(mockCategoryPageResultListValid)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService).getAllCategoryNames(pageNumber);

        }
        @DisplayName("PageIndexOutOfBoundsException")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#invalidRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testGetAllCategoriesWithValidTokenAndInvalidPage(int pageNumber) throws Exception{
            //test with valid token

            Mockito.when(categoriesService.getAllCategoryNames(pageNumber)).thenThrow(new PageIndexOutOfBoundsException());

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService).getAllCategoryNames(pageNumber);

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#validRange")
        @DisplayName("No token provided")
        void testGetAllCategoriesWithoutToken(int pageNumber) throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            PageResultResponse<String> mockCategoryPageResultList = CategoryControllerTest.generateMockCategoryList(pageNumber);
            Mockito.when(categoriesService.getAllCategoryNames(pageNumber)).thenReturn(mockCategoryPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService, Mockito.never()).getAllCategoryNames(pageNumber);

        }
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#validRange")
        @DisplayName("Token not valid")
        void testGetAllCategoriesWithInvalidToken(int pageNumber) throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);

            PageResultResponse<String> mockCategoryPageResultList = CategoryControllerTest.generateMockCategoryList(pageNumber);
            Mockito.when(categoriesService.getAllCategoryNames(pageNumber)).thenReturn(mockCategoryPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService, Mockito.never()).getAllCategoryNames(pageNumber);

        }
        @DisplayName("Valid authentication but role is NOT admin")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#validRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testAllCategoriesWithTokenRoleUser(int pageNumber) throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);

            PageResultResponse<String> mockCategoryList = CategoryControllerTest.generateMockCategoryList(pageNumber);
            Mockito.when(categoriesService.getAllCategoryNames(pageNumber)).thenReturn(mockCategoryList);

            mockMvc.perform(MockMvcRequestBuilders.get(url).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService, Mockito.never()).getAllCategoryNames(pageNumber);

        }

    }
    @Nested
    class testUpdateCategory{
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateCategoryCorrect() throws Exception {
            String id = "id123";
            CategoryDTO categoryDTORequest = createMockCategoryDTO();
            CategoryDTO expectedResponse = createMockCategoryDTO();

            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(categoryDTORequest))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService).edit(Mockito.any(), Mockito.any());
        }
        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateCategoryWithNonExistingId() throws Exception {
            String id = "id123";
            CategoryDTO categoryDTORequest = createMockCategoryDTO();

            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenThrow(new EntityNotFoundException());

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(categoryDTORequest))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Category Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService).edit(Mockito.any(), Mockito.any());
        }
        @Test
        @DisplayName("Name already exists")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateCategoryWithExistingName() throws Exception {
            String id = "id123";
            CategoryDTO categoryDTORequest = createMockCategoryDTO();

            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Entity With Name provided already Exits"));

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(categoryDTORequest))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Category Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService).edit(Mockito.any(), Mockito.any());
        }
        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testUpdateCategoryWithInvalidRole() throws  Exception{
            String id = "id123";
            CategoryDTO requestCategory = createMockCategoryDTO();
            requestCategory.setName("categoryrequest");
            CategoryDTO expectedResponse = createMockCategoryDTO();

            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(requestCategory))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Category TestDTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }
        @Test
        @DisplayName("Token not valid")
        void testUpdateCategoryWithInvalidToken() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            CategoryDTO categoryDTO = createMockCategoryDTO();
            CategoryDTO expectedResponse = createMockCategoryDTO();
            expectedResponse.setName("expected category");
            String id = "id123";

            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(categoryDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }
        @Test
        @DisplayName("Token not provided")
        void testUpdateCategoryWithoutToken() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            CategoryDTO categoryDTO = createMockCategoryDTO();
            CategoryDTO expectedResponse = createMockCategoryDTO();
            expectedResponse.setName("expected category");
            String id = "id123";

            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(categoryDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Activity Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTest#generateRequestsWithMissingAttributes")
        @DisplayName("Mandatory atributes missing")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateCategoryWithMissingAttributes(CategoryDTO requestWithMissingAttribute) throws Exception {
            CategoryDTO expectedResponse = createMockCategoryDTO();
            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);
            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Category Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTest#generateRequestsWithBrokenAttribute")
        @DisplayName("Broken attributes")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testUpdateCategoryWithBrokenAttributes(CategoryDTO requestWithBrokenAttribute) throws Exception{
            CategoryDTO expectedResponse = createMockCategoryDTO();
            Mockito.when(categoriesService.edit(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);
            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.put(url + "/{id}", id)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Category Test DTO"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService, Mockito.never()).edit(Mockito.any(), Mockito.any());
        }



    }
    @Nested
    class testDeleteCategory {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testDeleteCategoryWithValidToken() throws Exception{

            String id = "id123";
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService).deleteCategory(id);
        }
        @Test
        @DisplayName("Token not valid")
        void testDeleteCategoryWithInvalidToken() throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());
            String id = "id123";
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService,Mockito.never()).deleteCategory(Mockito.any());
        }
        @Test
        @DisplayName("Token not provided")
        void testDeleteCategoryWithoutToken() throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false); // Simulate: token was not provided.

            String id = "id123";

            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService,Mockito.never()).deleteCategory(Mockito.any());
        }
        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testDeleteCategoryWithNonExistingId() throws Exception{
            String id = "id123";
            Mockito.doThrow(new RuntimeException()).when(categoriesService).deleteCategory(id);

            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService).deleteCategory(id);


        }
        @Test
        @DisplayName("Valid authentication but role is NOT admin")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void testDeleteCategoryWithIncorrectUser() throws Exception{

            String id = "id123";
            Mockito.doThrow(new RuntimeException()).when(categoriesService).deleteCategory(id);
            mockMvc.perform(MockMvcRequestBuilders.delete(url + "/{id}", id))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(categoriesService,Mockito.never()).deleteCategory(Mockito.any());
        }
    }
    @Nested
    class testCreateCategory{
        @Test
        @DisplayName("Valid Case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testCreateCategoryWithCorrectAttributes() throws Exception{
            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();

            Mockito.when(categoriesService.save(Mockito.any())).thenReturn(categoryDTO);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .content(jsonMapper.writeValueAsString(categoryDTO))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(categoryDTO)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService).save(Mockito.any());
        }

        @Test
        @DisplayName("No token provided")
        void testCreateCategoryWithoutToken() throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();

            Mockito.when(categoriesService.save(Mockito.any())).thenReturn(categoryDTO);
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(categoryDTO))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("unaCategoria"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService,Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void testCreateCategorywithInvalidToken() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            CategoryDTO categoryDTO = CategoryControllerTest.createMockCategoryDTO();
            Mockito.when(categoriesService.save(Mockito.any())).thenReturn(categoryDTO);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .content(jsonMapper.writeValueAsString(categoryDTO))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService,Mockito.never()).save(Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#generateRequestsWithMissingAttribute")
        @DisplayName("Missing attribute")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testCreateCategoryWithMissingAttributes(CategoryDTO requestWithMissingAttributes) throws Exception {

            CategoryDTO category = CategoryControllerTest.createMockCategoryDTO();
            Mockito.when(categoriesService.save(Mockito.any())).thenReturn(category);

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .content(jsonMapper.writeValueAsString(requestWithMissingAttributes))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService,Mockito.never()).save((Mockito.any()));

        }
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.CategoryControllerTestv1#generateRequestsWithBrokenAttribute")
        @DisplayName("Invalid attribute format")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void testCreateCategoryWithBrokenAttribute(CategoryDTO requestWithBrokenAttribute) throws Exception {

            //Mockito.when(categoriesService.save(Mockito.any())).thenThrow(new Exception());

            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(jsonMapper.writeValueAsString(requestWithBrokenAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(categoriesService,Mockito.never()).save((Mockito.any()));

        }

        }


    static CategoryDTO createMockCategoryDTO(){
        CategoryDTO dto = new CategoryDTO();
        dto.setId("id123");
        dto.setName("unaCategoria");
        dto.setDescription("categoria de prueba");
        dto.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1658005269841-categoria1.png");
        return dto;
    }
    static List<CategoryDTO> generateRequestsWithMissingAttributes() {
        List<CategoryDTO> requestList = new ArrayList<>();
        CategoryDTO dto;

        dto = CategoryControllerTest.createMockCategoryDTO();
        dto.setName(null);
        requestList.add(dto);

        dto = CategoryControllerTest.createMockCategoryDTO();
        dto.setDescription(null);
        dto.setName(null);
        requestList.add(dto);

        dto = CategoryControllerTest.createMockCategoryDTO();
        dto.setImage(null);
        dto.setName(null);
        requestList.add(dto);

        return requestList;
    }
    static List<CategoryDTO> generateRequestsWithBrokenAttribute() {
        List<CategoryDTO> requestList = new ArrayList<>();
        CategoryDTO dto;

        dto = CategoryControllerTest.createMockCategoryDTO();
        dto.setName("%&-23");
        requestList.add(dto);

        dto = CategoryControllerTest.createMockCategoryDTO();
        dto.setName("111-)");
        requestList.add(dto);

        return requestList;
    }
    static PageResultResponse<String> generateMockCategoryList(int pageNumber){
        int cantPaginas = 10;
        int tamPagina = GlobalConstants.GLOBAL_PAGE_SIZE;
        List<String> list = new LinkedList<>();
        if (pageNumber < 0 || pageNumber > cantPaginas-1){
            list = new LinkedList<>();
        } else {
            for (int i = 0; i <tamPagina ; i++) {
                list.add("Mockito "+i);
            }
        }
        return new PageResultResponse<String>()
                .setContent(list)
                .setNext_page_url("Next Page")
                .setPrevious_page_url("Previous Page");
    }
    static Stream<Integer> validRange(){
        return IntStream.range(0,10).boxed();
    }
    static Stream<Integer> invalidRange(){
        return IntStream.range(10,20).boxed();
    }



}
