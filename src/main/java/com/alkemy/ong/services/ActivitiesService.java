package com.alkemy.ong.services;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.exception.ActivityException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ActivitiesService {

    public ResponseEntity<?> save (MultipartFile file, ActivityDTO dto) throws IOException;
}
