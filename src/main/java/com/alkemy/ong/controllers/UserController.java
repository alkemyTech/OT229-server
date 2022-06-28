package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

}
