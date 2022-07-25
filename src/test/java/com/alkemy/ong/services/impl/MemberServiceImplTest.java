package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.dto.MemberDTORequest;
import com.alkemy.ong.dto.MemberDTOResponse;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.entities.Member;
import com.alkemy.ong.exception.*;
import com.alkemy.ong.mappers.MemberMapper;
import com.alkemy.ong.repositories.MembersRepository;
import com.alkemy.ong.services.CloudStorageService;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = H2Configuration.class, loader = AnnotationConfigContextLoader.class)
class MemberServiceImplTest {

    private static final MemberMapper memberMapper = new MemberMapper();

    @Autowired
    private MembersRepository membersRepository;

    private static String existingMemberId = "";

    private static final int MOCK_MEMBERS = 5;

    private MemberServiceImpl memberService;

    @MockBean
    CloudStorageService cloudStorageService;

    @BeforeEach
    void startService() {
        this.memberService = new MemberServiceImpl(memberMapper, membersRepository, cloudStorageService);
    }

    @BeforeEach
    @Transactional
    void populateDB() {
        Member member = new Member();

        for (int i = 0; i < MOCK_MEMBERS; i++) {
            member = membersRepository.save(generateMockMember(i));

        }

        existingMemberId = member.getId();
    }

    @AfterEach
    @Transactional
    void emptyDB() {
        membersRepository.deleteAll();
    }

    @Nested
    class GetAllMembersTest {
        @Nested
        class NonPaginated {

            @Test
            @DisplayName("Full list returned")
            void test_1() {


                List<MemberDTOResponse> result = memberService.getAllMembers();
                assertEquals(MOCK_MEMBERS, result.size(), "the expected result size was returned");
                for (int i = 0; i < MOCK_MEMBERS; i++) {
                    assertNotNull(result.get(i), "member from list is not null");
                    assertTrue(result.get(i).getName().contains("Test"), "expected string value in member name");
                }
            }

            @Test
            @DisplayName("Empty list returned")
            void test_2() {
                emptyDB();

                assertDoesNotThrow(() -> {
                            List<MemberDTOResponse> result = memberService.getAllMembers();
                            assertEquals(0, result.size(), "No results were returned from repo");
                        }, "No Exception thrown by the service when receiving an empty list from the repo"
                );
            }

        }

        @Nested
        class Paginated {

            @Test
            @DisplayName("Full list returned")
            void test_1() {

                assertDoesNotThrow(
                        () -> {
                            PageResultResponse<MemberDTOResponse> result = memberService.getAllMembers(0);
                            assertEquals(MOCK_MEMBERS, result.getContent().size());
                            for (int i = 0; i < MOCK_MEMBERS; i++) {
                                assertNotNull(result.getContent().get(i), "Member from list not null");
                                assertTrue(result.getContent().get(i).getName().contains("Test"), "Expected string value in member name");
                            }
                        },
                        "Service didn't throw any exception"
                );


            }

            @Test
            @DisplayName("Index out of bounds")
            void test_2() {

                assertThrows(
                        PageIndexOutOfBoundsException.class,
                        () -> {
                            PageResultResponse<MemberDTOResponse> result = memberService.getAllMembers(-1);
                        },
                        "Expected exception thrown"
                );

            }
        }


    }

    @Nested
    class CreateMemberTest {

        @Nested
        class WithMultiPartFile {
            @Test
            @DisplayName("Success")
            void test_1() {

                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockFileURL = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadFile(Mockito.any())).thenReturn(mockFileURL);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest dto = generateNewMemberDTORequest(null, null);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.create(mockImageFile, dto);
                            assertNotNull(result, "resulting object is not null");
                            assertEquals(mockFileURL, result.getImage(), "Member's image attribute was correctly updated");
                        }
                        , "The service didn't throw any exception"
                );
                try {
                    Mockito.verify(cloudStorageService).uploadFile(mockImageFile);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Name Format Invalid")
            void test_2() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockFileURL = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadFile(Mockito.any())).thenReturn(mockFileURL);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest dto = generateNewMemberDTORequest("123", null);

                assertThrows(
                        RuntimeException.class,
                        () -> {
                            MemberDTOResponse result = memberService.create(mockImageFile, dto);
                        }
                        , "Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Corrupted file")
            void test_3() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());

                try {
                    Mockito.when(cloudStorageService.uploadFile(Mockito.any())).thenThrow(new CorruptedFileException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest dto = generateNewMemberDTORequest(null, null);

                assertThrows(
                        CorruptedFileException.class,
                        () -> {
                            MemberDTOResponse result = memberService.create(mockImageFile, dto);
                        },
                        "Expected Exception thrown"
                );
            }

            @Test
            @DisplayName("Image null")
            void test_4() {
                MemberDTORequest dto = generateNewMemberDTORequest(null, null);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.create(null, dto);
                            assertNotNull(result, "Resulting object is not null");
                            assertEquals(dto.getImage(), result.getImage(), "The image attr was not updated");
                        }
                        , "The service didn't throw any exception"
                );

                try {
                    Mockito.verify(cloudStorageService, Mockito.never()).uploadFile(Mockito.any());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("S3 Service problem")
            void test_5() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());

                try {
                    Mockito.when(cloudStorageService.uploadFile(Mockito.any())).thenThrow(new CloudStorageClientException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest dto = generateNewMemberDTORequest(null, null);

                assertThrows(
                        CloudStorageClientException.class,
                        () -> {
                            MemberDTOResponse result = memberService.create(mockImageFile, dto);
                        },
                        "Expected exception thrown"
                );
            }
        }

        @Nested
        class withImageBase64Encoded {

            @Test
            @DisplayName("Success")
            void test_1() {

                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                MemberDTORequest memberDTORequest = generateNewMemberDTORequest(null, mockEncodedImage);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.create(memberDTORequest);
                            assertNotNull(result, "Result object is not null");
                            assertEquals(mockUploadedFileUrl, result.getImage(), "The image attr was correctly updated");
                        },
                        "the service didn't throw any exception"
                );

                try {
                    Mockito.verify(cloudStorageService).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Incorrect name format")
            void test_2() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                MemberDTORequest memberDTORequest = generateNewMemberDTORequest("123", mockEncodedImage);

                assertThrows(
                        RuntimeException.class,
                        () -> {
                            MemberDTOResponse result = memberService.create(memberDTORequest);
                        }
                        , "Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Corrupted file")
            void test_3() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenThrow(new CorruptedFileException());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO encodedImageDTO = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                MemberDTORequest memberDTORequest = generateNewMemberDTORequest(null, encodedImageDTO);

                assertThrows(
                        CorruptedFileException.class,
                        () -> {
                            MemberDTOResponse result = memberService.create(memberDTORequest);
                        },
                        "Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Image null")
            void test_4() {
                EncodedImageDTO mockEncodedImageDTO = null;
                MemberDTORequest memberDTORequest = generateNewMemberDTORequest(null, null);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.create(memberDTORequest);
                            assertNotNull(result, "Result object is not null");
                            assertEquals(memberDTORequest.getImage(), result.getImage(), "The image attr was not updated");
                        },
                        "the service didn't throw any exception"
                );

                try {
                    Mockito.verify(cloudStorageService, Mockito.never()).uploadBase64File(Mockito.any(), Mockito.any());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("S3 service problem")
            void test_5() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenThrow(new CloudStorageClientException());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                MemberDTORequest request = generateNewMemberDTORequest(null, mockEncodedImage);

                assertThrows(
                        CloudStorageClientException.class,
                        () -> {
                            MemberDTOResponse result = memberService.create(request);
                        },
                        "Expected exception thrown"
                );
            }
        }
    }

    @Nested
    class editMEmberTest {
        @Nested
        class withMultiPartFile {
            @Test
            @DisplayName("Successful")
            void test_1() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }


                MemberDTORequest request = generateNewMemberDTORequest("Name updated", null);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.edit(mockImageFile, request, existingMemberId);
                            assertNotNull(result, "Result object is not null");
                            assertEquals("Name updated", result.getName(), "Attr has expected updated value");
                            assertEquals(mockUploadedFileUrl, result.getImage(), "The image attr was correctly updated");
                        },
                        "The service didn't throw any exception"
                );
                try {
                    Mockito.verify(cloudStorageService).uploadFile(mockImageFile);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Member not found")
            void test_2() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nonExistingId = "NonExistingId";
                Member entity = generateMockMember(940);
                entity.setId(nonExistingId);
                MemberDTORequest request = generateNewMemberDTORequest("Name updated", null);

                assertThrows(
                        MemberNotFoundException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(mockImageFile, request, nonExistingId);
                        },
                        "Expected exception thrown"
                );

                try {
                    Mockito.verify(cloudStorageService, Mockito.never()).uploadFile(mockImageFile);
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Name format invalid")
            void test_3() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest request = generateNewMemberDTORequest("New name 123", null);

                assertThrows(
                        RuntimeException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(mockImageFile, request, existingMemberId);
                        },
                        "Expected exception thrown"
                );
                try {
                    Mockito.verify(cloudStorageService, Mockito.never()).uploadFile(Mockito.any());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Corrupted file")
            void test_4() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());

                try {
                    Mockito.when(cloudStorageService.uploadFile(mockImageFile)).thenThrow(new CorruptedFileException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest request = generateNewMemberDTORequest("New name", null);

                assertThrows(
                        CorruptedFileException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(mockImageFile, request, existingMemberId);
                        },
                        "Expected Exception thrown"
                );
                Member entity = membersRepository.findById(existingMemberId).orElseThrow();
                assertNotEquals(request.getName(), entity.getName(), "Process was interrupted and changes were not saved");
            }

            @Test
            @DisplayName("Image null")
            void test_5() {
                MemberDTORequest request = generateNewMemberDTORequest("New name updated", null);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.edit(null, request, existingMemberId);
                            assertNotNull(result, "Result object is not null");
                            assertEquals(request.getImage(), result.getImage(), "The image attr was not updated");
                        },
                        "The service didn't throw any exception"
                );

                try {
                    Mockito.verify(cloudStorageService, Mockito.never()).uploadFile(Mockito.any());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("S3 service problem")
            void test_6() {
                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());

                try {
                    Mockito.when(cloudStorageService.uploadFile(mockImageFile)).thenThrow(new CloudStorageClientException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                MemberDTORequest request = generateNewMemberDTORequest("New updated name", null);

                assertThrows(
                        CloudStorageClientException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(mockImageFile, request, existingMemberId);
                        },
                        "Expected exception thrown"
                );
                Member entity = membersRepository.findById(existingMemberId).orElseThrow();
                assertNotEquals(request.getName(), entity.getName(), "Process was interrupted and changes were not saved");
            }
        }

        @Nested
        class WithImageBase64Encoded {
            @Test
            @DisplayName("Success")
            void test_1() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);

                MemberDTORequest request = generateNewMemberDTORequest("New test name", mockEncodedImage);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.edit(request, existingMemberId);
                            assertNotNull(result, "Result object is not null)");
                            assertEquals(mockUploadedFileUrl, result.getImage(), "The image attr was correctly updated");
                        },
                        "THe service didn't throw any exception"
                );

                try {
                    Mockito.verify(cloudStorageService).uploadBase64File(Mockito.any(), Mockito.any());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("Member not found")
            void test_2() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);

                String nonExistingId = "NonExistingId";
                MemberDTORequest request = generateNewMemberDTORequest("New member name", null);

                assertThrows(
                        MemberNotFoundException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(request, nonExistingId);
                        },
                        "Expected Exception thrown"
                );
            }

            @Test
            @DisplayName("Name format invalid")
            void test_3() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);

                MemberDTORequest request = generateNewMemberDTORequest("New member name 123", null);

                assertThrows(
                        RuntimeException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(request, existingMemberId);
                        },
                        "Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Corrupted file")
            void test_4() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenThrow(new CorruptedFileException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);

                MemberDTORequest request = generateNewMemberDTORequest("Another member name", mockEncodedImage);

                assertThrows(
                        CorruptedFileException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(request, existingMemberId);
                        },
                        "Expected exception thrown"
                );
                Member entity = membersRepository.findById(existingMemberId).orElseThrow();
                assertNotEquals(request.getName(), entity.getName(), "Process was interrupted and changes were not saved");
            }

            @Test
            @DisplayName("Image null")
            void test_5() {
                MemberDTORequest request = generateNewMemberDTORequest("Member testing name", null);

                assertDoesNotThrow(
                        () -> {
                            MemberDTOResponse result = memberService.edit(request, existingMemberId);
                            assertNotNull(result, "Result object is not null");
                            assertEquals(request.getImage(), result.getImage(), "The image attr was not updated");
                        },
                        "The service didn't throw any exception"
                );
                try {
                    Mockito.verify(cloudStorageService, Mockito.never()).uploadFile(Mockito.any());
                } catch (CloudStorageClientException | CorruptedFileException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("S3 service problem")
            void test_6() {
                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";

                try {
                    Mockito.when(cloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenThrow(new CloudStorageClientException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);

                MemberDTORequest request = generateNewMemberDTORequest("Last member test name", mockEncodedImage);

                assertThrows(
                        CloudStorageClientException.class,
                        () -> {
                            MemberDTOResponse result = memberService.edit(request, existingMemberId);
                        },
                        "Expected exception thrown"
                );

                Member entity = membersRepository.findById(existingMemberId).orElseThrow();
                assertNotEquals(request.getName(), entity.getName(), "Process was interrupted and changes were not saved");
            }
        }
    }

    @Nested
    @Transactional
    class DeleteMemberTest {
        @Test
        @DisplayName("Success")
        void test_1() {

            assertDoesNotThrow(
                    () -> {
                        String result = memberService.deleteMember(existingMemberId);
                        assertTrue(result.contains("Successfully deleted member"));
                    },
                    "The service didn't throw any exception"
            );
        }

        @Test
        @DisplayName("Member not found")
        void test_2() {
            String nonExistingId = "nonExistingId";

            assertThrows(
                    NotFoundException.class,
                    () -> {
                        String result = memberService.deleteMember(nonExistingId);
                        assertFalse(result.contains("Successfully deleted member"));
                    },
                    "Expected exception thrown"
            );
        }

        @Test
        @DisplayName("S3 service CloudStorageClientException Exception")
        void test_3() {
            String fileURL = "testURL";
            String result = "Successfully deleted member";

            try {
                Mockito.doThrow(new CloudStorageClientException()).when(cloudStorageService).deleteFileFromS3Bucket(Mockito.any());
            } catch (CloudStorageClientException | FileNotFoundOnCloudException e) {
                throw new RuntimeException(e);
            }

            assertThrows(
                    EntityImageProcessingException.class,
                    () -> {
                        String response = memberService.deleteMember(existingMemberId);
                        assertFalse(response.contains(result));
                    },
                    "Expected Exception thrown"
            );
        }

    }

    private static MemberDTORequest generateNewMemberDTORequest(String name, EncodedImageDTO encodedImage) {
        MemberDTORequest request = new MemberDTORequest();
        if (name != null) {
            request.setName(name);
        } else {
            request.setName("Test");
        }
        request.setImage(null);
        request.setFacebookUrl("facebook.com/test");
        request.setInstagramUrl("instagram.com/test");
        request.setLinkedinUrl("linkedin.com/test");
        request.setDescription("testDescription");
        request.setEncoded_image(encodedImage);

        return request;
    }


    static Member generateMockMember(int idx) {
        Member member = new Member();

        member.setName("Test" + idx);
        member.setFacebookUrl("facebook.com/test");
        member.setInstagramUrl("instagram.com/test");
        member.setLinkedinUrl("linkedin.com/test");
        member.setImage("https://cohorte-junio-a192d78b.s3.amazonaws.com/1657462814446-ezeTest.txt");
        member.setDescription("testDescription");

        return member;
    }
}
