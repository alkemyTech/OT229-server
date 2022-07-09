package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.exception.MemberNotFoundException;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping("/{id}")
    public ResponseEntity editMember(MultipartFile file, @Valid @ModelAttribute MemberDTORequest request , @PathVariable String id) {
        try {
            return new ResponseEntity<>(memberService.edit(file,request,id), HttpStatus.OK);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
