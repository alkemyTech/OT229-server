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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
}
