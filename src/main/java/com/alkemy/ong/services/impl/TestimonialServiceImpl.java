package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.entities.Testimonial;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.EntityImageProcessingException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.mappers.TestimonialMapper;
import com.alkemy.ong.repositories.TestimonialRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.TestimonialService;
import javassist.NotFoundException;
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

    @Override
    public TestimonialDTOResponse update(String id, MultipartFile file, TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException, NotFoundException {
        Boolean exists = repository.existsById(id);
        if (!exists)throw new NotFoundException("Error: Testimonial with id " + id + " was not found");
        Testimonial testimonialFound= repository.getById(id);
        if(file != null && !file.isEmpty()){
            testimonialFound.setImage(amazonS3Service.uploadFile(file));
        }
        if(!request.getName().isEmpty()) testimonialFound.setName(request.getName());
        if(!request.getContent().isEmpty()) testimonialFound.setContent(request.getContent());

        repository.save(testimonialFound);
        return mapper.testimonialEntity2DTOResponse(testimonialFound);
    }

    @Override
    public String delete(String id) throws NotFoundException, CloudStorageClientException, FileNotFoundOnCloudException {
       Boolean exists = repository.existsById(id);
        if(!exists)throw new NotFoundException("Error: Testimonial with id " + id + " was not found");
       Testimonial testimonial=repository.getById(id);
       deleteTestimonialImageFromCloudStorage(testimonial);
       repository.deleteById(id);
       return "Successfully deleted testimonial with id " + id;
    }
    private void deleteTestimonialImageFromCloudStorage(Testimonial testimonial)throws CloudStorageClientException, FileNotFoundOnCloudException {
        String urlImage = testimonial.getImage();
        if (urlImage != null && !urlImage.isEmpty()) {
            try {
                amazonS3Service.deleteFileFromS3Bucket(urlImage);
            } catch (EntityImageProcessingException e) {
                if (!(e instanceof FileNotFoundOnCloudException)) {
                    throw e;
                }
            }
        }
    }}
