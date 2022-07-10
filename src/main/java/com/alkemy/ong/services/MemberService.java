package com.alkemy.ong.services;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import javassist.NotFoundException;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface MemberService {
    MemberDTOResponse create(MultipartFile file,MemberDTORequest request) throws CloudStorageClientException, CorruptedFileException;
    String deleteMember(String id) throws NotFoundException, CloudStorageClientException, FileNotFoundOnCloudException;
    List<MemberDTOResponse> getAllMembers();

    /**
     * Performs a paginated search for all the Member entries in the database and returns them along with the urls
     * to get the previous and next page results.
     *
     * <p>Page size and sorting criteria are read from global constants.
     *
     * @param pageNumber    the index of the page to be retrieved.
     * @return  the list of Members. If the provided index exceeds the last existing index, an empty list will
     *          be returned, and the previous page url attribute will point to the last available page.
     * @throws PageIndexOutOfBoundsException    if the index is not a positive integer.
     */
    PageResultResponse<MemberDTOResponse> getAllMembers(int pageNumber) throws PageIndexOutOfBoundsException;

}

