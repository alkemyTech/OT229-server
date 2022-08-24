package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.*;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.services.OrganizationService;
import com.alkemy.ong.services.SlidesService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(GlobalConstants.Endpoints.ORGANIZATION_PUBLIC_INFO)
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SlidesService slidesService;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<ReducedOrganizationDTO>> getAll() {
        List<ReducedOrganizationDTO> dtos = organizationService.getAll();
        return ResponseEntity.ok().body(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            ReducedOrganizationDTO organization = organizationService.getById(id);
            List<SlidesEntityDTO> slides = this.slidesService.findByOrganization(id);
            return ResponseEntity.ok().body(new OrganizationInfoResponse(organization, slides));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> updateOrganization(@Valid @RequestBody OrganizationDTORequest organizationDTO) throws CloudStorageClientException, CorruptedFileException {

        try{
            OrganizationDTO org = organizationService.updateOrganization(organizationDTO);

            return ResponseEntity.ok().body(org);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
