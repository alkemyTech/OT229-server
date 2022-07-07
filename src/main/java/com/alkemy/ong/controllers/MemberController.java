package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.MEMBERS)
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public ResponseEntity createMember(@Valid @ModelAttribute MemberDTORequest request){
        try {
            return new ResponseEntity<>(memberService.create(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
