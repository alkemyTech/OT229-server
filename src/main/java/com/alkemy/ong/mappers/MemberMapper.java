package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.MemberDTO;
import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.entities.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member dtoRequest2MemberEntity(MemberDTORequest memberDTORequest){
        Member member = new Member();
        member.setName(memberDTORequest.getName());
        member.setImage(memberDTORequest.getImage());
        member.setDescription(memberDTORequest.getDescription());
        member.setFacebookUrl(memberDTORequest.getFacebookUrl());
        member.setInstagramUrl(memberDTORequest.getInstagramUrl());
        member.setLinkedinUrl(memberDTORequest.getLinkedinUrl());
        return  member;

    }

    public MemberDTOResponse memberEntity2DTOResponse (Member member){
        MemberDTOResponse memberDTOResponse = new MemberDTOResponse();
        memberDTOResponse.setName(member.getName());
        memberDTOResponse.setImage(member.getImage());
        memberDTOResponse.setDescription(member.getDescription());
        memberDTOResponse.setFacebookUrl(member.getFacebookUrl());
        memberDTOResponse.setInstagramUrl(member.getInstagramUrl());
        memberDTOResponse.setLinkedinUrl(member.getLinkedinUrl());
        return memberDTOResponse;

    }
    public MemberDTO memberEntity2DTOMember(Member member){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setDescription(member.getDescription());
        memberDTO.setImage(member.getImage());
        memberDTO.setName(member.getName());
        memberDTO.setFacebookUrl(member.getFacebookUrl());
        memberDTO.setInstagramUrl(member.getInstagramUrl());
        memberDTO.setLinkedinUrl(member.getLinkedinUrl());
        return memberDTO;
    }
}
