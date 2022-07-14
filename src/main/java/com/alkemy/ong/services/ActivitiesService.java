package com.alkemy.ong.services;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.dto.ActivityDTORequest;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.ActivityNotFoundException;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import org.springframework.web.multipart.MultipartFile;

public interface ActivitiesService {

    public ActivityDTO save (MultipartFile file, ActivityDTO dto) throws CloudStorageClientException, ActivityNamePresentException, CorruptedFileException;
    public ActivityDTO save (ActivityDTORequest dto) throws CloudStorageClientException, ActivityNamePresentException, CorruptedFileException;


    public ActivityDTO edit (MultipartFile file, ActivityDTO dto, String id) throws CloudStorageClientException, ActivityNamePresentException, ActivityNotFoundException, CorruptedFileException;
    public ActivityDTO edit (ActivityDTORequest dto, String id) throws CloudStorageClientException, ActivityNamePresentException, ActivityNotFoundException, CorruptedFileException;


}
