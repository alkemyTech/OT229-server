package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.ContactDTORequest;
import com.alkemy.ong.dto.ContactDTOResponse;
import com.alkemy.ong.entities.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

  public Contact DTORequest2ContactEntity(ContactDTORequest request){
Contact contact = new Contact();
contact.setEmail(request.getEmail());
contact.setName(request.getName());
contact.setMessage(request.getMessage());
contact.setPhone(request.getPhone());
 return contact;
  }

  public ContactDTOResponse ContactEntityToDTOResponse(Contact contact,String message){
ContactDTOResponse response = new ContactDTOResponse();
response.setEmail(contact.getEmail());
response.setName(contact.getName());
response.setConfirmation(message);
return response;
  }
}
