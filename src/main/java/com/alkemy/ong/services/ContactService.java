package com.alkemy.ong.services;

import com.alkemy.ong.dto.ContactDTO;
import com.alkemy.ong.dto.ContactDTORequest;
import com.alkemy.ong.dto.ContactDTOResponse;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.Contact;

import java.util.List;

public interface ContactService {
    public ContactDTOResponse create(ContactDTORequest request) throws Exception;
    List<ContactDTO> getAll();
}
