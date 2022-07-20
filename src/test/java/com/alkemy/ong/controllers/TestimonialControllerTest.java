package com.alkemy.ong.controllers;

import com.alkemy.ong.configuration.SwaggerConfiguration;
import com.alkemy.ong.dto.*;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.security.configuration.SecurityConfiguration;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.security.service.impl.UserDetailsServiceImpl;
import com.alkemy.ong.services.TestimonialService;
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
import javassist.NotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@WebMvcTest(TestimonialController.class)
@Import({SecurityConfiguration.class, BCryptPasswordEncoder.class, SwaggerConfiguration.class})
public class TestimonialControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    JwtService jwtService;
    @MockBean
    private TestimonialService service;
    String page = GlobalConstants.PAGE_INDEX_PARAM;
    ObjectMapper jsonMapper = new ObjectMapper();

    @Nested
    class createTestimonialTest {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception{
            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();

            Mockito.when(service.create(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.TESTIMONIALS)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).create(Mockito.any());
        }


        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test3() throws  Exception{
            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();

            Mockito.when(service.create(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.TESTIMONIALS)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void test4() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();

            Mockito.when(service.create(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.TESTIMONIALS )
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void test5() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();

            Mockito.when(service.create(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.TESTIMONIALS)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }


        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.TestimonialControllerTest#generateRequestMissingMandatoryAttributes")
        @DisplayName("MissingAttributes")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test6(TestimonialDTORequest requestWithMissingAttribute) throws Exception {

            TestimonialDTOResponse response = TestimonialControllerTest.generateTestimonialDTOResponse();
            Mockito.when(service.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.TESTIMONIALS)
                            .content(jsonMapper.writeValueAsString( requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }

    }

    @Nested
    class updateTestimonialTest {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception {
            String id = "1707ID";
            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();

            Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.TESTIMONIALS + "?id="+ id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(expectedResponse)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).update(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws Exception {
            String id = "1707ID";
            TestimonialDTORequest request = generateTestimonialDTORequest();

            Mockito.when(service.update(Mockito.any(), Mockito.any())).thenThrow(new NotFoundException("Error: Testimonial with id" + id + " was not found"));

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.TESTIMONIALS + "?id="+ id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).update(Mockito.any(), Mockito.any());
        }


        @Test
        @DisplayName("Invalid role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws  Exception{
            String id = "1707ID";
            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();

            Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);


            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.TESTIMONIALS + "?id="+ id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).update(Mockito.any(), Mockito.any());
        }


        @Test
        @DisplayName("Token not valid")
        void test5() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());
            String id = "1707ID";
            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();


            Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.TESTIMONIALS + "?id="+ id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).update(Mockito.any(), Mockito.any());
        }

        @Test
        @DisplayName("Token not provided")
        void test6() throws Exception{
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);
            String id = "1707ID";
            TestimonialDTORequest request = generateTestimonialDTORequest();
            TestimonialDTOResponse expectedResponse = generateTestimonialDTOResponse();


            Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.put(GlobalConstants.Endpoints.TESTIMONIALS + "?id="+ id)
                            .content(jsonMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Testimonial Test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).update(Mockito.any(), Mockito.any());
        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.TestimonialControllerTest#generateRequestMissingMandatoryAttributes")
        @DisplayName("Missing Attributes")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test7(TestimonialDTORequest requestWithMissingAttribute) throws Exception {
            String id="?id=12000";
            TestimonialDTOResponse response = TestimonialControllerTest.generateTestimonialDTOResponse();
            Mockito.when(service.create(Mockito.any())).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.post(GlobalConstants.Endpoints.TESTIMONIALS +id)
                            .content(jsonMapper.writeValueAsString( requestWithMissingAttribute))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("Somos"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).create(Mockito.any());
        }

    }
    @Nested
    class testGetAllTestimonials {


        @DisplayName("Valid Case")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.TestimonialControllerTest#validRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test1(int pageNumber) throws Exception{

            PageResultResponse<TestimonialDTOResponse> mockTestimonialPageResultList = TestimonialControllerTest.generateMockTestimonialList(pageNumber);
            PageResultResponse<TestimonialDTOResponse> mockTestimonialPageResultListValid = TestimonialControllerTest.generateMockTestimonialList(0);
            Mockito.when(service.getAllTestimonies(pageNumber)).thenReturn(mockTestimonialPageResultList);
            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.TESTIMONIALS).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(jsonMapper.writeValueAsString(mockTestimonialPageResultListValid)))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).getAllTestimonies(pageNumber);

        }
        @DisplayName("PageIndexOutOfBoundsException")
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.TestimonialControllerTest#invalidRange")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test2(int pageNumber) throws Exception{

            Mockito.when(service.getAllTestimonies(pageNumber)).thenThrow(new PageIndexOutOfBoundsException());

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.TESTIMONIALS).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("test"))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).getAllTestimonies(pageNumber);

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.TestimonialControllerTest#validRange")
        @DisplayName("No token provided")
        void test3(int pageNumber) throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);

            PageResultResponse<TestimonialDTOResponse> mockTestimonialPageResultList = TestimonialControllerTest.generateMockTestimonialList(pageNumber);
            Mockito.when(service.getAllTestimonies(pageNumber)).thenReturn(mockTestimonialPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.TESTIMONIALS).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("test"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(service, Mockito.never()).getAllTestimonies(pageNumber);

        }
        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.TestimonialControllerTest#validRange")
        @DisplayName("Token not valid")
        void test4(int pageNumber) throws Exception{

            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);

            PageResultResponse<TestimonialDTOResponse> mockTestimonialPageResultList = TestimonialControllerTest.generateMockTestimonialList(pageNumber);
            Mockito.when(service.getAllTestimonies(pageNumber)).thenReturn(mockTestimonialPageResultList);

            mockMvc.perform(MockMvcRequestBuilders.get(GlobalConstants.Endpoints.TESTIMONIALS).param(page, String.valueOf(pageNumber)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString("TEST"))))
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(service, Mockito.never()).getAllTestimonies(pageNumber);

        }


    }
    @Nested
    class deleteTestimonialTest {
        @Test
        @DisplayName("Valid case")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test1() throws Exception {
            String idParam = "1707d";
            String expectedResponse = "Successfully deleted testimonial with id " + "id";

            Mockito.when(service.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.TESTIMONIALS +"?id="+idParam))
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andExpect(MockMvcResultMatchers.content().string(expectedResponse))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service).delete(Mockito.any());
        }

        @Test
        @DisplayName("Non-existing ID")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_ADMIN)
        void test2() throws Exception{
            String id = "d1707";
            Mockito.doThrow(new NotFoundException("")).when(service).delete(id);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.TESTIMONIALS +"?id="+id))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());
            Mockito.verify(service).delete(id);


        }

        @Test
        @DisplayName("Invalid user role")
        @WithMockUser(username = "mock.user@mockmail.mock", authorities = GlobalConstants.ROLE_USER)
        void test4() throws Exception {
            String idParam = "22";
            String expectedResponse =  "Successfully deleted testimonial with id " + "id";

            Mockito.when(service.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER +"?id="+ idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).delete(Mockito.any());
        }

        @Test
        @DisplayName("No token provided")
        void test5() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(false);
            String idParam = "22";
            String expectedResponse =  "Successfully deleted testimonial with id " + "id";

            Mockito.when(service.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER +"?id="+ idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).delete(Mockito.any());
        }

        @Test
        @DisplayName("Token not valid")
        void test6() throws Exception {
            Mockito.when(jwtService.isBearer(Mockito.any())).thenReturn(true);
            Mockito.when(jwtService.verify(Mockito.any())).thenThrow(new Exception());

            String idParam = "22";
            String expectedResponse =  "Successfully deleted testimonial with id " + "id";

            Mockito.when(service.delete(Mockito.any())).thenReturn(expectedResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete(GlobalConstants.Endpoints.USER +"?id="+ idParam))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(expectedResponse))))
                    .andDo(MockMvcResultHandlers.print());

            Mockito.verify(service, Mockito.never()).delete(Mockito.any());
        }
    }

    private static TestimonialDTOResponse generateTestimonialDTOResponse(){
        TestimonialDTOResponse response = new TestimonialDTOResponse();
       response.setName("Testimonial DTO tests");
        response.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657325315906-tutorias.jpg");
       response.setContent("Hi!");

        return response;
    }

    private static TestimonialDTORequest generateTestimonialDTORequest(){
        TestimonialDTORequest request = new TestimonialDTORequest();
        request.setName("Testimonial Test request");
        request.setContent("Hi!");
        request.setEncoded_image(
                new EncodedImageDTO(
                        "iVBORw0KGgoAAAANSUhEUgAAABAAAAAFCAIAAADDivseAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAA5SURBVBhXZctBDgAhEAJB//9pBJkQMvZhU6x6EoDRc2ZbeeSvwdpuLkdGzP6pb9To1ul+4HraTAYuqMKPcY0zc9EAAAAASUVORK5CYII=",
                        "sample.png"
                )
        );

        return request;
    }

    private static List<TestimonialDTORequest> generateRequestMissingMandatoryAttributes(){
        List<TestimonialDTORequest> requests = new ArrayList<>();
        TestimonialDTORequest singleRequest;

        // CASE 1: Missing name
        singleRequest = generateTestimonialDTORequest();
        singleRequest.setName(null);
        requests.add(singleRequest);

        // CASE 2: Missing content
        singleRequest = generateTestimonialDTORequest();
        singleRequest.setContent(null);
        requests.add(singleRequest);


        return requests;
    }

    private static List<TestimonialDTORequest> generateRequestWithBrokenAttributes() {
        List<TestimonialDTORequest> requests = new ArrayList<>();
        TestimonialDTORequest singleRequest;

        singleRequest = generateTestimonialDTORequest();
        singleRequest.setName(" ");
        requests.add(singleRequest);

        singleRequest = generateTestimonialDTORequest();
        singleRequest.setContent(" ");
        requests.add(singleRequest);

        return requests;
    }
       static PageResultResponse<TestimonialDTOResponse> generateMockTestimonialList(int pageNumber){
        int cantPaginas = 10;
        int tamPagina = GlobalConstants.GLOBAL_PAGE_SIZE;
        List<TestimonialDTOResponse> list = new LinkedList<>();
        if (pageNumber < 0 || pageNumber > cantPaginas-1){
            list = new LinkedList<>();
        } else {
            for (int i = 0; i <tamPagina ; i++) {
                list.add(generateTestimonialDTOResponse());
            }
        }
        return new PageResultResponse<TestimonialDTOResponse>()
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


