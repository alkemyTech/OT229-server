package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.ContactDTO;
import com.alkemy.ong.dto.ContactDTORequest;
import com.alkemy.ong.dto.ContactDTOResponse;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.Contact;
import com.alkemy.ong.services.ContactService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.Endpoints.CONTACT)
public class ContactController {

    @Autowired
    private ContactService service;

    @PostMapping
    public ResponseEntity<?> createContact(@ModelAttribute ContactDTORequest request) {
        try {
            return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAll(){

        return new ResponseEntity<>(service.getAll(),HttpStatus.OK);

    }
}