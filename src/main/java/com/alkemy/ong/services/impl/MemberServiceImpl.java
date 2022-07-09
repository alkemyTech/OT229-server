package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.entities.Member;
import com.alkemy.ong.exception.MemberNotFoundException;
import com.alkemy.ong.mappers.MemberMapper;
import com.alkemy.ong.repositories.MembersRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CloudStorageService amazonS3Service;

    @Override
    public MemberDTOResponse create(MemberDTORequest request) throws Exception {
        Member member;
        //Este regx es para validar el formato name
        String regx = "^[\\p{L} .'-]+$";
        String name = request.getName();
        if (Pattern.matches(regx,name)) {
            member = memberMapper.dtoRequest2MemberEntity(request);
            membersRepository.save(member);
            return memberMapper.memberEntity2DTOResponse(member);
        } else {
            //Utilice este metodo para validar el String
            throw new Exception("Formato de Nombre invalido");
        }


    }

    @Override
    public MemberDTOResponse edit(MultipartFile file, MemberDTORequest request, String id) throws Exception {
        Optional<Member> memberFound = membersRepository.findById(id);

        if (!memberFound.isPresent()) {
            throw new MemberNotFoundException("M<ember with the provided ID was not found over the system");
        }

        if (file != null && !file.isEmpty()) {
            request.setImage(amazonS3Service.uploadFile(file));
        } else {
            request.setImage(null);
        }

        Member entity = memberMapper.dtoRequest2MemberEntity(request);
        Member entitySaved = membersRepository.save(entity);

        MemberDTOResponse response = memberMapper.memberEntity2DTOResponse(entitySaved);

        return response;
    }
}
