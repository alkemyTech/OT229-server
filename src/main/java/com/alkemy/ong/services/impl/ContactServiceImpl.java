package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.ContactDTO;
import com.alkemy.ong.dto.ContactDTORequest;
import com.alkemy.ong.dto.ContactDTOResponse;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.Contact;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.mappers.ContactMapper;
import com.alkemy.ong.repositories.ContactRepository;
import com.alkemy.ong.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

        @Autowired
        ContactMapper mapper;

        @Autowired
        ContactRepository repository;



    @Override
    public ContactDTOResponse create(ContactDTORequest request) throws Exception {

        if (request.getEmail().isEmpty()) throw new Exception("Enter an email please");
        if (request.getName().isEmpty()) throw new Exception("Enter a name please");

        Contact contact = mapper.DTORequest2ContactEntity(request);
        repository.save(contact);


        String message = "Hi " + contact.getName() +
                "! We received your form, shortly you will receive a response to the provided email box: "
                + contact.getEmail();

        return mapper.ContactEntityToDTOResponse(contact, message);

      }
    public List<ContactDTO> getAll() {

        List<ContactDTO> contactDTOS = new LinkedList<>();
        for(Contact contact : repository.findAll()){
            contactDTOS.add(mapper.ContactEntityToDTO(contact));
        }
        contactDTOS.sort(Comparator.comparing(ContactDTO::getEmail).reversed());
        return contactDTOS;

    }
}