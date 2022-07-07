package com.alkemy.ong.services;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    MemberDTOResponse create(MemberDTORequest request) throws Exception;
}

