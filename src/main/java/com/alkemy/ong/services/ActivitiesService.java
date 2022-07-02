package com.alkemy.ong.services;

import com.alkemy.ong.dto.ActivityDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ActivitiesService {

    public ActivityDTO save (MultipartFile file, ActivityDTO dto) throws IOException;
}
