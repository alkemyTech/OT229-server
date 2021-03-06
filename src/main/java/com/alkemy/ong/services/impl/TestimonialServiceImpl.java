package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.entities.Testimonial;
import com.alkemy.ong.exception.*;
import com.alkemy.ong.mappers.PageResultResponseBuilder;
import com.alkemy.ong.mappers.TestimonialMapper;
import com.alkemy.ong.repositories.TestimonialRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.TestimonialService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    public TestimonialServiceImpl (TestimonialRepository repo, TestimonialMapper mapper,CloudStorageService cloudStorageService){
        this.repository=repo;
        this.mapper= mapper;
        this.amazonS3Service= cloudStorageService;
    }

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
    public TestimonialDTOResponse create(TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException {

        Testimonial testimonial= mapper.dtoRequest2TestimonialEntity(request);
        if(request.getEncoded_image() != null){
            testimonial.setImage(amazonS3Service.uploadBase64File(
                    request.getEncoded_image().getEncoded_string(),
                    request.getEncoded_image().getFile_name()
            ));
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
    public TestimonialDTOResponse update(String id, TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException, NotFoundException {
        Boolean exists = repository.existsById(id);
        if (!exists)throw new NotFoundException("Error: Testimonial with id " + id + " was not found");
        Testimonial testimonialFound= repository.getById(id);
        if(request.getEncoded_image() != null){
            testimonialFound.setImage(amazonS3Service.uploadBase64File(
                    request.getEncoded_image().getEncoded_string(),
                    request.getEncoded_image().getFile_name()
            ));
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

    @Override
    public PageResultResponse<TestimonialDTOResponse> getAllTestimonies(int pageNumber) throws PageIndexOutOfBoundsException {
        if (pageNumber < 0) {
            throw new PageIndexOutOfBoundsException("The Page number must be 0 or positive");
        }
        Pageable pageRequest = PageRequest.of(
                pageNumber,
                GlobalConstants.GLOBAL_PAGE_SIZE,
                Sort.by(GlobalConstants.TESTIMONIAL_SORT_ATTRIBUTE)
        );
        Page<Testimonial> resultPage = repository.findAll(pageRequest);

        return new PageResultResponseBuilder<Testimonial, TestimonialDTOResponse>()
                .from(resultPage)
                .mapWith(mapper::testimonialEntity2DTOResponse)
                .build();
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
