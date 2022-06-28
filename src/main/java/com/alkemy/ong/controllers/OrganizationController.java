package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.services.OrganizationService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

//@PreAuthorize("permitAll()") // Descomentar cuando se trabaje en el ticket 73
@RestController
@RequestMapping(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<List<ReducedOrganizationDTO>> getAll() {
        List<ReducedOrganizationDTO> dtos = organizationService.getAll();
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReducedOrganizationDTO> getById(@PathVariable String id) {
        ReducedOrganizationDTO dto = organizationService.getById(id);
        return ResponseEntity.ok().body(dto);
    }

    //@PreAuthorize("hasRole('ADMIN')") // Descomentar cuando se trabaje en el ticket 73
    @PostMapping
    public ResponseEntity<OrganizationDTO> updateOrganization(@RequestParam(value = "file", required = false) MultipartFile image,
                                                              @ModelAttribute OrganizationDTO organizationDTO){

        try{
            OrganizationDTO org = organizationService.updateOrganization(image, organizationDTO);

            return ResponseEntity.ok().body(org);
        } catch (IOException e){
            return ResponseEntity.notFound().build();
        }
    }
}
