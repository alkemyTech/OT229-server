package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO userEntity2DTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setImage(user.getPhoto());
        return userDTO;
    }
}
