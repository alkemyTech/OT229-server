package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.entities.Testimonial;
import org.springframework.stereotype.Component;

@Component
    public class TestimonialMapper {

        public Testimonial dtoRequest2TestimonialEntity(TestimonialDTORequest request){
            Testimonial testimonial = new Testimonial();
            testimonial.setName(request.getName());
            testimonial.setContent(request.getContent());
            return testimonial;

        }
        public TestimonialDTOResponse testimonialEntity2DTOResponse(Testimonial testimonial){
            TestimonialDTOResponse response = new TestimonialDTOResponse();
            response.setName(testimonial.getName());
            response.setImage(testimonial.getImage());
            response.setContent(testimonial.getContent());
            return response;
        }
    }

