package com.alkemy.ong.services;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import javassist.NotFoundException;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface MemberService {
    MemberDTOResponse create(MultipartFile file,MemberDTORequest request) throws CloudStorageClientException, CorruptedFileException;
    String deleteMember(String id) throws NotFoundException, CloudStorageClientException, FileNotFoundOnCloudException;
    List<MemberDTOResponse> getAllMembers();
}

