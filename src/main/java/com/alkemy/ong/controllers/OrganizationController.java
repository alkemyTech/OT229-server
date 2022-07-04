package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.OrganizationInfoResponse;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.services.OrganizationService;
import com.alkemy.ong.services.SlidesService;
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
    @Autowired
    private SlidesService slidesService;

    @GetMapping
    public ResponseEntity<List<ReducedOrganizationDTO>> getAll() {
        List<ReducedOrganizationDTO> dtos = organizationService.getAll();
        return ResponseEntity.ok().body(dtos);
    }

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

    //@PreAuthorize("hasRole('ADMIN')") // Descomentar cuando se trabaje en el ticket 73
    @PostMapping
    public ResponseEntity<?> updateOrganization(@RequestParam(value = "file", required = false) MultipartFile image,
                                                              @ModelAttribute OrganizationDTO organizationDTO){

        try{
            OrganizationDTO org = organizationService.updateOrganization(image, organizationDTO);

            return ResponseEntity.ok().body(org);
        }catch (AmazonS3Exception e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
