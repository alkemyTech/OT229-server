package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.services.OrganizationService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("authenticated")
@RestController
@RequestMapping(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAll() {
        List<OrganizationDTO> dtos = organizationService.getAll();
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getById(@PathVariable String id) {
        OrganizationDTO dto = organizationService.getById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public String actualizarOrganizacion(){
        return "hola";
    }
}
