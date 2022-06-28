package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.dto.UserDTORequest;
import com.alkemy.ong.exception.AmazonS3Exception;
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
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return userService.delete(id);
        } catch (NotFoundException e) {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return e.getMessage();
        }
    }
   
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(){

        return new ResponseEntity<>(userService.getAll(),HttpStatus.OK);

    }

 @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestParam(value = "file", required = false) MultipartFile multipartfile, @ModelAttribute UserDTORequest userDTORequest) {
        try {
            return new ResponseEntity<>(userService.updateUser(multipartfile, userDTORequest), HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
         } catch (AmazonS3Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/auth/me")
    public ResponseEntity<UserDTO> getMe(@RequestHeader("authorization") String jwt) throws Exception{
        return new ResponseEntity<>(userService.getMe(jwt), HttpStatus.OK);
    }
}
