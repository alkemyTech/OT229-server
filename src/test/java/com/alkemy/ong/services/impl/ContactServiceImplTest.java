package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.*;
import com.alkemy.ong.entities.Contact;
import com.alkemy.ong.mappers.ContactMapper;
import com.alkemy.ong.repositories.ContactRepository;
import com.alkemy.ong.services.EmailService;
import com.alkemy.ong.utility.GlobalConstants;
import com.amazonaws.services.mq.model.BadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
public class ContactServiceImplTest {


    private static final ContactMapper contactMapper = new ContactMapper();
    @Autowired
    private ContactRepository contactRepository;

    private EmailService emailService= Mockito.mock(EmailServiceImp.class);
    private static String existingContactId = "";
    private static final int numberOfMocksContacts = 5;

    @BeforeEach
    @Transactional
    void populateDatabase() {
        Contact contact = new Contact();
        for (int i = 1; i <= numberOfMocksContacts; i++) {
            contact = contactRepository.save(generateMockContact(i));
        }
        existingContactId = contact.getId();
    }

    @AfterEach
    @Transactional
    void emptyDatabase() {
        contactRepository.deleteAll();
    }

    @Nested
    class GetAllTest {

        @Test
        @DisplayName("Populated list returned")
        void test1() {
            // SETUP
            ContactServiceImpl contactService = new ContactServiceImpl(
                    contactMapper, contactRepository, emailService
            );
            // TEST
            List<ContactDTO> resultList = contactService.getAll();
            assertEquals(numberOfMocksContacts, resultList.size(), "The expected number of results were returned.");
            for (int i = 0; i < numberOfMocksContacts; i++) {
                assertNotNull(resultList.get(i), "Object from result list is not null");

            }
        }

        @Test
        @DisplayName("Empty list returned")
        void test2() {
            // SETUP
            ContactServiceImpl contactService = new ContactServiceImpl(
                    contactMapper, contactRepository, emailService
            );
            // TEST
            emptyDatabase();
            assertDoesNotThrow(
                    () -> {
                        List<ContactDTO> resultList = contactService.getAll();
                        assertEquals(0, resultList.size(), "Zero results were returned from the repository.");
                    }
                    , "Service did not throw an exception when receiving and empty list from the repository."
            );
        }

    }

    @Nested
    class saveContactTest {

        @Test
        @DisplayName("Successful save")
        void test1() throws Exception {
            String nameContact = "Contact test";
            ContactServiceImpl service = new ContactServiceImpl(contactMapper, contactRepository, emailService);
            ContactDTORequest request = generateMockContactDTORequest();
            Contact entity = contactMapper.DTORequest2ContactEntity(request);
            ContactDTOResponse response = generateMockContactDTOResponse(request);

            try {
                Mockito.when(service.create(request)).thenReturn(response);
            }catch(Exception e){
                throw new Exception(e);
            }
            assertDoesNotThrow(
                    () -> {
                        ContactDTOResponse result = service.create(request);
                        assertNotNull(result, "The object is not null");
                        assertEquals(nameContact, response.getName());

                    }
                    , "The service did not throw any exception."
            );
            try {
                Mockito.verify(contactRepository.save(entity));
                Mockito.verify(emailService).sendEmail(request.getEmail(), GlobalConstants.TEMPLATE_CONTACT);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        @Test
        @DisplayName("emailService throw Exception")
        void test2() throws Exception {

            ContactServiceImpl service = new ContactServiceImpl(contactMapper, contactRepository, emailService);
            ContactDTORequest request = generateMockContactDTORequest();
            Contact entity = contactMapper.DTORequest2ContactEntity(request);

            Mockito.when(service.create(request)).thenThrow(new Exception ("send email failed"));
            assertThrows(
                   Exception.class,
                    () -> {
                        ContactDTOResponse response = service.create(request);
                    }
                    ,"Expected exception thrown"
            );
            try {
                Mockito.verify(contactRepository, Mockito.never()).save(entity);
                Mockito.verify(emailService).sendEmail(request.getEmail(), GlobalConstants.TEMPLATE_CONTACT);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        @Test
        @DisplayName("Broken attributes")
        void test3() throws Exception {

            ContactServiceImpl service = new ContactServiceImpl(contactMapper, contactRepository, emailService);
            ContactDTORequest request = generateMockContactDTORequestWithBrokenAttributes();
            Contact entity = contactMapper.DTORequest2ContactEntity(request);
            ContactDTOResponse response = generateMockContactDTOResponse(request);


           Mockito.when(service.create(request)).thenReturn(response);

            assertThrows(
                   Exception.class,
                    () -> {
                        ContactDTOResponse result = service.create(request);
                    }
                    ,"Expected exception thrown"
            );

                Mockito.verify(contactRepository, Mockito.never()).save(entity);
                Mockito.verify(emailService,Mockito.never()).sendEmail(request.getEmail(), GlobalConstants.TEMPLATE_CONTACT);

        }
    }

    static Contact generateMockContact(int indexStamp) {
        Contact contact = new Contact();
        contact.setMessage("Hello Test" + indexStamp);
        contact.setPhone(65645L + indexStamp);
        contact.setName("Test" + indexStamp);
        contact.setEmail("test" + indexStamp + "@test.com");
        return contact;
    }

    static ContactDTORequest generateMockContactDTORequest() {
        ContactDTORequest request = new ContactDTORequest();
        request.setName("Contact test");
        request.setPhone(89745L);
        request.setEmail("requestContact@test.com");
        request.setMessage("Test");
        return request;
    }
    static ContactDTORequest generateMockContactDTORequestWithBrokenAttributes() {
        ContactDTORequest request = new ContactDTORequest();
        request.setName("");
        request.setPhone(89745L);
        request.setEmail("");
        request.setMessage("Test");
        return request;
    }

    static ContactDTOResponse generateMockContactDTOResponse(ContactDTORequest request) {
        ContactDTOResponse response = new ContactDTOResponse();
        response.setName(request.getName());
        response.setEmail(request.getEmail());
        response.setConfirmation("test ok");
        return response;
    }
}




