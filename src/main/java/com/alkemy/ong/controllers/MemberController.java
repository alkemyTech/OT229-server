package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(GlobalConstants.Endpoints.MEMBERS)
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public ResponseEntity createMember(@RequestParam(value = "file") MultipartFile file, @ModelAttribute MemberDTORequest memberDTORequest){
        try {
            return new ResponseEntity<>(memberService.create(file,memberDTORequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> deleteMember(@PathVariable String id)throws CloudStorageClientException, FileNotFoundOnCloudException {
        try {
            return new ResponseEntity<>(memberService.deleteMember(id),HttpStatus.NO_CONTENT);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
