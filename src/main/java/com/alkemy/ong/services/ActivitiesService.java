package com.alkemy.ong.services;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.ActivityNotFoundException;
import com.alkemy.ong.exception.AmazonS3Exception;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ActivitiesService {

    public ActivityDTO save (MultipartFile file, ActivityDTO dto) throws IOException, AmazonS3Exception, ActivityNamePresentException;

    public ActivityDTO edit (MultipartFile file, ActivityDTO dto, String id) throws IOException, AmazonS3Exception, ActivityNamePresentException, ActivityNotFoundException;
}
