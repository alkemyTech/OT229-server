package com.alkemy.ong.services;

import com.alkemy.ong.dto.ContactDTORequest;
import com.alkemy.ong.dto.ContactDTOResponse;

public interface ContactService {
    public ContactDTOResponse create(ContactDTORequest request) throws Exception;
}
