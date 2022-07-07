package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.entities.Member;
import com.alkemy.ong.mappers.MemberMapper;
import com.alkemy.ong.repositories.MembersRepository;
import com.alkemy.ong.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private MemberMapper memberMapper;

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
}
