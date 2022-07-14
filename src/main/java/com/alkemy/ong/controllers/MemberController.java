package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.exception.MemberNotFoundException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.MEMBERS)
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public ResponseEntity createMember(@RequestParam(value = "file" , required = false) MultipartFile file,@Valid @ModelAttribute MemberDTORequest memberDTORequest){
        try {
            return new ResponseEntity<>(memberService.create(file,memberDTORequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> deleteMember(@RequestParam("id")String id)throws CloudStorageClientException, FileNotFoundOnCloudException {
        try {
            return new ResponseEntity<>(memberService.deleteMember(id),HttpStatus.NO_CONTENT);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllMembers(@RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int pageNumber) throws PageIndexOutOfBoundsException {
        return ResponseEntity.ok(this.memberService.getAllMembers(pageNumber));
    }

    @PutMapping("/{id}")
    public ResponseEntity editMember(@RequestParam(value = "file", required = false) MultipartFile file, @Valid @ModelAttribute MemberDTORequest request , @PathVariable String id) {
        try {
            return new ResponseEntity<>(memberService.edit(file,request,id), HttpStatus.OK);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
