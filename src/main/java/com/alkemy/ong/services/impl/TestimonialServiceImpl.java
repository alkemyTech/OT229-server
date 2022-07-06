package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.entities.Testimonial;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.mappers.TestimonialMapper;
import com.alkemy.ong.repositories.TestimonialRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TestimonialServiceImpl implements TestimonialService {

    @Autowired
    private TestimonialRepository repository;

    @Autowired
    private TestimonialMapper mapper;

    @Autowired
    private CloudStorageService amazonS3Service;

    @Override
    public TestimonialDTOResponse create(MultipartFile file, TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException {

        Testimonial testimonial= mapper.dtoRequest2TestimonialEntity(request);
        if(file != null && !file.isEmpty()){
            testimonial.setImage(amazonS3Service.uploadFile(file));
        }
        repository.save(testimonial);
        return mapper.testimonialEntity2DTOResponse(testimonial);

    }
}
