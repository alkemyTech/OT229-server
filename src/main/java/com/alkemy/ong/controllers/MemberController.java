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

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.MEMBERS)
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public ResponseEntity createMember(@Valid @RequestBody MemberDTORequest memberDTORequest){
        try {
            return new ResponseEntity<>(memberService.create(memberDTORequest), HttpStatus.CREATED);
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

    // EL MÉTODO DE ABAJO DEBERÍA MODIFICAR A ESTE, PERO LO DEJO COMO ESTABA Y COMENTADO (PORQ GENERA CONFLICTO) PARA LA DEMO.
//    @GetMapping
//    public ResponseEntity<?> getAllMembers() {
//        return ResponseEntity.ok(this.memberService.getAllMembers());
//    }

    @GetMapping
    public ResponseEntity<?> getAllMembers(@RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int pageNumber) throws PageIndexOutOfBoundsException {
        return ResponseEntity.ok(this.memberService.getAllMembers(pageNumber));
    }

    @PutMapping("/{id}")
    public ResponseEntity editMember(@Valid @RequestBody MemberDTORequest request , @PathVariable String id) {
        try {
            return new ResponseEntity<>(memberService.edit(request,id), HttpStatus.OK);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
