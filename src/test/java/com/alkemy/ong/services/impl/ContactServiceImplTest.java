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
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
public class ContactServiceImplTest {


    private static final ContactMapper contactMapper = new ContactMapper();
    @Autowired
    private ContactRepository contactRepository;

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
            EmailService emailService = Mockito.mock(EmailService.class);
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
            EmailService emailService = Mockito.mock(EmailService.class);
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
            EmailService emailService = Mockito.mock(EmailService.class);
            ContactServiceImpl contactService = new ContactServiceImpl(contactMapper, contactRepository, emailService);
            ContactDTORequest request = generateMockContactDTORequest();

            assertDoesNotThrow(
                    () -> {
                        ContactDTOResponse result = contactService.create(request);
                        assertNotNull(result, "The object is not null");
                        assertEquals(request.getName(), result.getName());

                    }
                    , "The contactService did not throw any exception."
            );
            try {
                Mockito.verify(emailService).sendEmail(request.getEmail(), GlobalConstants.TEMPLATE_CONTACT);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        @Test
        @DisplayName("emailService throw Exception")
        void test2() throws Exception {
            EmailService emailService = Mockito.mock(EmailService.class);
            ContactServiceImpl service = new ContactServiceImpl(contactMapper, contactRepository, emailService);
            ContactDTORequest request = generateMockContactDTORequest();

            Mockito.when(service.create(request)).thenThrow(new IOException ("send email failed"));
            assertThrows(
                   IOException.class,
                    () -> {
                        ContactDTOResponse response = service.create(request);
                    }
                    ,"Expected exception thrown"
            );
            try {
                Mockito.verify(emailService).sendEmail(request.getEmail(), GlobalConstants.TEMPLATE_CONTACT);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        @Test
        @DisplayName("Broken attributes")
        void test3() throws Exception {
            EmailService emailService = Mockito.mock(EmailService.class);
            ContactServiceImpl service = new ContactServiceImpl(contactMapper, contactRepository, emailService);
            ContactDTORequest request = generateMockContactDTORequestWithBrokenAttributes();

            assertThrows(
                   Exception.class,
                    () -> {
                        ContactDTOResponse result = service.create(request);
                    }
                    ,"Expected exception thrown"
            );

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

}




