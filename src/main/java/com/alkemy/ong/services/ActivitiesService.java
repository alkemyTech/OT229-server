package com.alkemy.ong.services;

import com.alkemy.ong.dto.ActivityDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ActivitiesService {

    public ActivityDTO save (MultipartFile file, ActivityDTO dto) throws IOException;

    public ActivityDTO edit (MultipartFile file, ActivityDTO dto, String id) throws IOException;
}
