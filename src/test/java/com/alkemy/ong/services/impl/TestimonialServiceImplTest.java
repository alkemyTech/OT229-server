package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.entities.Testimonial;
import com.alkemy.ong.mappers.TestimonialMapper;
import com.alkemy.ong.repositories.TestimonialRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.transaction.Transactional;
import java.util.Date;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
public class TestimonialServiceImplTest {

    private static TestimonialMapper mapper = new TestimonialMapper();

    @Autowired
    private TestimonialRepository repository;


    private static String existingTestimonialById = "";
    private static final int numberOfMockTestimonials = 5;

    @BeforeEach
    @Transactional
    void populateDatabase() {
        Testimonial testimonial = new Testimonial();
        for (int i = 1; i <= numberOfMockTestimonials; i++) {
           testimonial = repository.save(generateMockTestimonial(i));
        }
        existingTestimonialById = testimonial.getId();
    }

    @AfterEach
    @Transactional
    void emptyDatabase() {
        repository.deleteAll();
    }

    static Testimonial generateMockTestimonial(int indedxStamp){
      Testimonial testimonial = new Testimonial();
       testimonial.setId("TestimonialID " + indedxStamp);
        testimonial.setName("testimonial " + indedxStamp);
       testimonial.setContent("testimonial content " + indedxStamp);
       testimonial.setImage("image.jpg " + indedxStamp);
       testimonial.setTimeStamp(new Date());

        return testimonial;
    }

    private static TestimonialDTOResponse generateANewTestimonialDTOResponse(Testimonial testimonial){
       TestimonialDTOResponse response = new TestimonialDTOResponse();
        response.setName(testimonial.getName());
        response.setContent(testimonial.getContent());
        response.setImage(testimonial.getImage());

        return response;
    }

    private static TestimonialDTORequest generateANewTestimonialDTORequest(EncodedImageDTO encodedImage){
       TestimonialDTORequest request = new TestimonialDTORequest();
       request.setName("testimonial");
        request.setContent("Test save content");
        request.setEncoded_image(encodedImage);

        return request;
    }
}