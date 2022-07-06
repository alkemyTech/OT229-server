package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(GlobalConstants.Endpoints.USER)
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping
    public String deleteUser(@RequestParam("id") String id, HttpServletResponse httpServletResponse) {
        try {
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
            return userService.delete(id);
        } catch (NotFoundException e) {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return e.getMessage();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(){

        return new ResponseEntity<>(userService.getAll(),HttpStatus.OK);

    }

 @PutMapping
    public ResponseEntity<?> updateUser(@RequestParam(value = "file", required = false) MultipartFile multipartfile,@Valid @ModelAttribute UserDTORequest userDTORequest) {
        try {
            return new ResponseEntity<>(userService.updateUser(multipartfile, userDTORequest), HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
         } catch (CloudStorageClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
