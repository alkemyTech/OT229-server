package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.MemberDTO;
import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.entities.Member;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.EntityImageProcessingException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.mappers.MemberMapper;
import com.alkemy.ong.repositories.MembersRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

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
            if (file != null && !file.isEmpty()){
                request.setImage(cloudStorageService.uploadFile(file));
            } else {
                request.setImage(null);
            }
            member = memberMapper.dtoRequest2MemberEntity(request);
            membersRepository.save(member);
            return memberMapper.memberEntity2DTOResponse(member);
        } else {
            //Utilice este metodo para validar el String
            throw new RuntimeException("Formato de Nombre invalido");
        }

    }
    @Override
    @Transactional
    public MemberDTO deleteMember(String id) throws EntityNotFoundException,CloudStorageClientException {
        Member memberToDelete = membersRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Member with the provided id not found."));
        try{
            this.deleteMemberImageFromCloudStorage(memberToDelete);
        } catch (FileNotFoundOnCloudException e){

        }
        MemberDTO memberDTO = this.memberMapper.memberEntity2DTOMember(memberToDelete);
        this.membersRepository.delete(memberToDelete);
        return memberDTO;
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
}
