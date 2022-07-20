package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.exception.MemberNotFoundException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.services.MemberService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.MEMBERS)
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Operation(summary = "Create and save a new Member entity", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Returns the Member DTO created successfully", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDTOResponse.class))}),

                    @ApiResponse(responseCode = "400", description = "Returns Member name format invalid", content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Name format invalid"))})
            }
    )
    @PostMapping
    public ResponseEntity createMember(@Valid @RequestBody MemberDTORequest memberDTORequest){
        try {
            return new ResponseEntity<>(memberService.create(memberDTORequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a Member entity" , security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Returns successful deletion message", content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Successfully deleted member with id ${id}"))}),

                    @ApiResponse(responseCode = "404", description = "Returns Member not found message", content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Error: Member wih id: ${id} was not found"))})
            }
    )
    @DeleteMapping
    public ResponseEntity<?> deleteMember(@RequestParam("id")String id)throws CloudStorageClientException, FileNotFoundOnCloudException {
        try {
            return new ResponseEntity<>(memberService.deleteMember(id),HttpStatus.NO_CONTENT);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all members in the system" , security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Returns all the members in the system in paginated format", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = PageResultResponse.class))}),

                    @ApiResponse(responseCode = "400", description = "Returns error message page number must be positive", content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class , example = "Page number must be positive"))})
            }
    )
    @GetMapping
    public ResponseEntity<?> getAllMembers(@RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int pageNumber) throws PageIndexOutOfBoundsException {
        return ResponseEntity.ok(this.memberService.getAllMembers(pageNumber));
    }

    @Operation(summary = "Modify a specific member in the system" , security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Returns the DTO of the modified member", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDTOResponse.class))}),

                    @ApiResponse(responseCode = "404", description = "Returns error message member not found", content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Member with the provided ID was not found over the system"))}),

                    @ApiResponse(responseCode = "400", description = "Returns error message name format invalid", content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class, example = "Name format invalid"))}),
            }
    )
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
