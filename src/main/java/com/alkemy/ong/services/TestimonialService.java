package com.alkemy.ong.services;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import javassist.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface TestimonialService {
    TestimonialDTOResponse create(MultipartFile file, TestimonialDTORequest request)throws CloudStorageClientException, CorruptedFileException;
    TestimonialDTOResponse update(String id,MultipartFile file, TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException, NotFoundException;
}