package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.entities.Member;
import com.alkemy.ong.exception.*;
import com.alkemy.ong.mappers.MemberMapper;
import com.alkemy.ong.mappers.PageResultResponseBuilder;
import com.alkemy.ong.repositories.MembersRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CloudStorageService cloudStorageService;

    @Override
    public MemberDTOResponse create(MultipartFile file, MemberDTORequest request) throws CloudStorageClientException, CorruptedFileException {
        Member member;
        //Este regx es para validar el formato name
        String regx = "^[\\p{L} .'-]+$";
        String name = request.getName();
        if (Pattern.matches(regx,name)) {
            member = memberMapper.dtoRequest2MemberEntity(request);
            if (file != null && !file.isEmpty()){
                member.setImage(cloudStorageService.uploadFile(file));
            } else {
                member.setImage(null);
            }
            membersRepository.save(member);
            return memberMapper.memberEntity2DTOResponse(member);
        } else {
            //Utilice este metodo para validar el String
            throw new RuntimeException("Name format invalid");
        }

    }

    @Override
    public MemberDTOResponse create(MemberDTORequest request) throws CloudStorageClientException, CorruptedFileException {
        Member member;
        //Este regx es para validar el formato name
        String regx = "^[\\p{L} .'-]+$";
        String name = request.getName();
        if (Pattern.matches(regx,name)) {
            member = memberMapper.dtoRequest2MemberEntity(request);
            if (request.getEncoded_image() != null){
                member.setImage(cloudStorageService.uploadBase64File(
                        request.getEncoded_image().getEncoded_string(),
                        request.getEncoded_image().getFile_name()
                ));
            } else {
                member.setImage(null);
            }
            membersRepository.save(member);
            return memberMapper.memberEntity2DTOResponse(member);
        } else {
            //Utilice este metodo para validar el String
            throw new RuntimeException("Name format invalid");
        }
    }

    @Override
    @Transactional
    public String deleteMember(String id) throws NotFoundException,CloudStorageClientException,FileNotFoundOnCloudException {
        Boolean exists = membersRepository.existsById(id);
        if (!exists) throw new NotFoundException("Error: Member wih id: " +id+ " was not found");
        Member member = membersRepository.getById(id);
        deleteMemberImageFromCloudStorage(member);
        membersRepository.deleteById(id);
        return "Successfully deleted member with id" + id;
    }

    private void deleteMemberImageFromCloudStorage(Member member)throws CloudStorageClientException, FileNotFoundOnCloudException{
        String urlImage = member.getImage();
        if (urlImage != null && !urlImage.equals("")){
            try {
                this.cloudStorageService.deleteFileFromS3Bucket(urlImage);
            } catch (EntityImageProcessingException e){
                if (!(e instanceof FileNotFoundOnCloudException)){
                    throw e;
                }
            }
        }
    }
    public List<MemberDTOResponse> getAllMembers(){
        List <MemberDTOResponse> memberDTOResponses = new LinkedList<>();
        for (Member member: membersRepository.findAll()){
            memberDTOResponses.add(memberMapper.memberEntity2DTOResponse(member));
        }
        memberDTOResponses.sort(Comparator.comparing(MemberDTOResponse::getName));
        return memberDTOResponses;
    }

    @Override
    public PageResultResponse<MemberDTOResponse> getAllMembers(int pageNumber) throws PageIndexOutOfBoundsException {
        if (pageNumber < 0) {
            throw new PageIndexOutOfBoundsException("Page number must be positive");
        }
        Pageable pageRequest = PageRequest.of(
                pageNumber,
                GlobalConstants.GLOBAL_PAGE_SIZE,
                Sort.by(GlobalConstants.MEMBERS_SORT_ATTRIBUTE)
        );
        Page<Member> springDataResultPage = this.membersRepository.findAll(pageRequest);
        return new PageResultResponseBuilder<Member, MemberDTOResponse>()
                .from(springDataResultPage)
                .mapWith(this.memberMapper::memberEntity2DTOResponse)
                .build();
    }

    @Override
    public MemberDTOResponse edit(MultipartFile file, MemberDTORequest request, String id) throws MemberNotFoundException, RuntimeException, Exception {
        String regx = "^[\\p{L} .'-]+$";
        String name = request.getName();

        if (!Pattern.matches(regx, name)) {
            throw new RuntimeException("Name format invalid");
        }

        Optional<Member> memberFound = membersRepository.findById(id);

        if (!memberFound.isPresent()) {
            throw new MemberNotFoundException("Member with the provided ID was not found over the system");
        }

        if (file != null && !file.isEmpty()) {
            request.setImage(cloudStorageService.uploadFile(file));
        } else {
            request.setImage(null);
        }

        Member modifiedEntity = memberMapper.editEntity(memberFound.get(), request);
        membersRepository.save(modifiedEntity);
        MemberDTOResponse response = memberMapper.memberEntity2DTOResponse(modifiedEntity);

        return response;
    }

    @Override
    public MemberDTOResponse edit(MemberDTORequest request, String id) throws MemberNotFoundException, Exception {
        String regx = "^[\\p{L} .'-]+$";
        String name = request.getName();

        if (!Pattern.matches(regx, name)) {
            throw new RuntimeException("Name format invalid");
        }

        Optional<Member> memberFound = membersRepository.findById(id);

        if (!memberFound.isPresent()) {
            throw new MemberNotFoundException("Member with the provided ID was not found over the system");
        }

        if (request.getEncoded_image() != null) {
            request.setImage(cloudStorageService.uploadBase64File(
                    request.getEncoded_image().getEncoded_string(),
                    request.getEncoded_image().getFile_name()
            ));
        } else {
            request.setImage(null);
        }

        Member modifiedEntity = memberMapper.editEntity(memberFound.get(), request);
        membersRepository.save(modifiedEntity);
        MemberDTOResponse response = memberMapper.memberEntity2DTOResponse(modifiedEntity);

        return response;
    }


}
