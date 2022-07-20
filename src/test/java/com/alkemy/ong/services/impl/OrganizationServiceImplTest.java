package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.OrganizationDTORequest;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.mappers.OrganizationMapper;
import com.alkemy.ong.repositories.OrganizationsRepository;
import com.alkemy.ong.services.CloudStorageService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
class OrganizationServiceImplTest {

    private static final OrganizationMapper organizationMapper = new OrganizationMapper();
    @Autowired
    private OrganizationsRepository organizationsRepository;

    private static String existingOrgId = "";
    private static final int numberOfMockOrgs = 5;

    @BeforeEach
    @Transactional
    void populateDatabase() {
        Organization organization = new Organization();
        for (int i = 1; i <= numberOfMockOrgs; i++) {
            organization = organizationsRepository.save( generateMockOrganization(i) );
        }
        existingOrgId = organization.getId();
    }

    @AfterEach
    @Transactional
    void emptyDatabase() {
        organizationsRepository.deleteAll();
    }

    @Nested
    class GetAllTest {

        @Test
        @DisplayName("Populated list returned")
        void test1() {
            // SETUP
            CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
            OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                    organizationMapper,
                    organizationsRepository,
                    mockCloudStorageService
            );
            // TEST
            List<ReducedOrganizationDTO> resultList = organizationService.getAll();
            assertEquals(numberOfMockOrgs, resultList.size(), "The expected number of results were returned.");
            for (int i = 0; i < numberOfMockOrgs; i++) {
                assertNotNull(resultList.get(i), "Object from result list is not null");
                assertTrue(resultList.get(i).getName().contains("name"), "Attribute has expected mock value." );
            }
        }

        @Test
        @DisplayName("Empty list returned")
        void test2() {
            // SETUP
            CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
            OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                    organizationMapper,
                    organizationsRepository,
                    mockCloudStorageService
            );
            // TEST
            emptyDatabase();
            assertDoesNotThrow(
                    () -> {
                        List<ReducedOrganizationDTO> resultList = organizationService.getAll();
                        assertEquals(0, resultList.size(), "Zero results were returned from the repository.");
                    }
                    , "Service did not throw an exception when receiving and empty list from the repository."
            );
        }

    }

    @Nested
    class GetByIdTest {

        @Test
        @DisplayName("Organization found")
        void test1() {
            // SETUP
            CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
            OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                    organizationMapper,
                    organizationsRepository,
                    mockCloudStorageService
            );
            // TEST
            ReducedOrganizationDTO result = organizationService.getById(existingOrgId);
            assertNotNull(result, "Object from result list is not null");
            assertTrue(result.getName().contains("name"), "Attribute has expected mock value." );
        }

        @Test
        @DisplayName("Organization not found")
        void test2() {
            // SETUP
            CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
            OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                    organizationMapper,
                    organizationsRepository,
                    mockCloudStorageService
            );
            // TEST
            assertThrows(
                    RuntimeException.class,
                    () -> {
                        ReducedOrganizationDTO result = organizationService.getById("aNonExistingId");
                    }
                    , "Expected exception thrown when entity not found."
            );
        }

    }

    @Nested
    class UpdateOrganizationMultipartTest {

        @Nested
        class OrganizationFoundTest {

            @Nested
            class ImageFilePresentTest {

                @Test
                @DisplayName("Valid case")
                void test1() {
                    // SETUP
                    CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                    OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                            organizationMapper,
                            organizationsRepository,
                            mockCloudStorageService
                    );
                    MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                    String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";
                    try {
                        Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                    Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                    OrganizationDTO dtoWithUpdatedInfo = organizationMapper.organizationEntity2OrganizationDTO(orgWithUpdatedInfo);
                    String updatedName = "Updated Name";
                    dtoWithUpdatedInfo.setName(updatedName);
                    // TEST
                    assertDoesNotThrow(
                            () -> {
                                OrganizationDTO result = organizationService.updateOrganization(mockImageFile, dtoWithUpdatedInfo);
                                assertNotNull(result, "Result object is not null.");
                                assertEquals(updatedName, result.getName(), "Attribute has expected updated value.");
                                assertEquals(mockUploadedFileUrl, result.getImage(), "The image attribute was accurately updated.");
                            }
                            , "The service did not throw any exception."
                    );
                    try {
                        Mockito.verify(mockCloudStorageService).uploadFile(mockImageFile);
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Test
                @DisplayName("Corrupted file")
                void test2() {
                    // SETUP
                    CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                    OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                            organizationMapper,
                            organizationsRepository,
                            mockCloudStorageService
                    );
                    MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                    try {
                        Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenThrow(new CorruptedFileException());
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                    Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                    OrganizationDTO dtoWithUpdatedInfo = organizationMapper.organizationEntity2OrganizationDTO(orgWithUpdatedInfo);
                    String updatedName = "Updated Name";
                    dtoWithUpdatedInfo.setName(updatedName);
                    // TEST
                    assertThrows(
                            CorruptedFileException.class,
                            () -> {
                                OrganizationDTO result = organizationService.updateOrganization(mockImageFile, dtoWithUpdatedInfo);
                            }
                            , "Expected exception thrown"
                    );
                    Organization orgEntity = organizationsRepository.findById(existingOrgId).orElseThrow();
                    assertNotEquals(dtoWithUpdatedInfo.getName(), orgEntity.getName(), "Process was interrupted and changes were not saved.");
                }

                @Test
                @DisplayName("S3 Service problem")
                void test3() {
                    // SETUP
                    CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                    OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                            organizationMapper,
                            organizationsRepository,
                            mockCloudStorageService
                    );
                    MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                    try {
                        Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenThrow(new CloudStorageClientException());
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                    Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                    OrganizationDTO dtoWithUpdatedInfo = organizationMapper.organizationEntity2OrganizationDTO(orgWithUpdatedInfo);
                    String updatedName = "Updated Name";
                    dtoWithUpdatedInfo.setName(updatedName);
                    // TEST
                    assertThrows(
                            CloudStorageClientException.class,
                            () -> {
                                OrganizationDTO result = organizationService.updateOrganization(mockImageFile, dtoWithUpdatedInfo);
                            }
                            , "Expected exception thrown"
                    );
                    Organization orgEntity = organizationsRepository.findById(existingOrgId).orElseThrow();
                    assertNotEquals(dtoWithUpdatedInfo.getName(), orgEntity.getName(), "Process was interrupted and changes were not saved.");
                }

            }

            @Test
            @DisplayName("Image file null")
            void test4() {
                // SETUP
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                        organizationMapper,
                        organizationsRepository,
                        mockCloudStorageService
                );
                Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                OrganizationDTO dtoWithUpdatedInfo = organizationMapper.organizationEntity2OrganizationDTO(orgWithUpdatedInfo);
                String updatedName = "Updated Name";
                dtoWithUpdatedInfo.setName(updatedName);
                // TEST
                assertDoesNotThrow(
                        () -> {
                            OrganizationDTO result = organizationService.updateOrganization(null, dtoWithUpdatedInfo);
                            assertNotNull(result, "Result object is not null.");
                            assertEquals(updatedName, result.getName(), "Attribute has expected updated value.");
                            assertEquals(orgWithUpdatedInfo.getImage(), result.getImage(), "The image attribute was not updated.");
                        }
                        , "The service did not throw any exception."
                );
                try {
                    Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(Mockito.any());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        @Test
        @DisplayName("Organization not found")
        void test5() {
            // SETUP
            CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
            OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                    organizationMapper,
                    organizationsRepository,
                    mockCloudStorageService
            );
            Organization orgWithNonExistingId = generateMockOrganization(666);
            orgWithNonExistingId.setId("NonExistingId");
            OrganizationDTO dtoWithNonExistingId = organizationMapper.organizationEntity2OrganizationDTO(orgWithNonExistingId);
            MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
            // TEST
            assertThrows(RuntimeException.class,
                    () -> {
                        OrganizationDTO result = organizationService.updateOrganization(mockImageFile, dtoWithNonExistingId);
                    }
                    , "Expected exception thrown."
            );
            try {
                Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(Mockito.any());
            } catch (CorruptedFileException | CloudStorageClientException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Nested
    class UpdateOrganizationNoMultipartTest {

        @Nested
        class OrganizationFoundTest {

            @Nested
            class ImageFilePresentTest {

                @Test
                @DisplayName("Valid case")
                void test1() {
                    // SETUP
                    CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                    OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                            organizationMapper,
                            organizationsRepository,
                            mockCloudStorageService
                    );
                    String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                    String mockEncodedImageFileName = "test_file.mock";
                    String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";
                    try {
                        Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                                .thenReturn(mockUploadedFileUrl);
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                    Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                    EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                    OrganizationDTORequest dtoWithUpdatedInfo = orgDtoToRequestDtoMapper(orgWithUpdatedInfo, mockEncodedImage);
                    String updatedName = "Updated Name";
                    dtoWithUpdatedInfo.setName(updatedName);
                    // TEST
                    assertDoesNotThrow(
                            () -> {
                                OrganizationDTO result = organizationService.updateOrganization(dtoWithUpdatedInfo);
                                assertNotNull(result, "Result object is not null.");
                                assertEquals(updatedName, result.getName(), "Attribute has expected updated value.");
                                assertEquals(mockUploadedFileUrl, result.getImage(), "The image attribute was accurately updated.");
                            }
                            , "The service did not throw any exception."
                    );
                    try {
                        Mockito.verify(mockCloudStorageService).uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName);
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Test
                @DisplayName("Corrupted file")
                void test2() {
                    // SETUP
                    CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                    OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                            organizationMapper,
                            organizationsRepository,
                            mockCloudStorageService
                    );
                    String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                    String mockEncodedImageFileName = "test_file.mock";
                    try {
                        Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                                .thenThrow(new CorruptedFileException());
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                    Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                    EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                    OrganizationDTORequest dtoWithUpdatedInfo = orgDtoToRequestDtoMapper(orgWithUpdatedInfo, mockEncodedImage);
                    String updatedName = "Updated Name";
                    dtoWithUpdatedInfo.setName(updatedName);
                    // TEST
                    assertThrows(
                            CorruptedFileException.class,
                            () -> {
                                OrganizationDTO result = organizationService.updateOrganization(dtoWithUpdatedInfo);
                            }
                            , "Expected exception thrown"
                    );
                    Organization orgEntity = organizationsRepository.findById(existingOrgId).orElseThrow();
                    assertNotEquals(dtoWithUpdatedInfo.getName(), orgEntity.getName(), "Process was interrupted and changes were not saved.");
                }

                @Test
                @DisplayName("S3 Service problem")
                void test3() {
                    // SETUP
                    CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                    OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                            organizationMapper,
                            organizationsRepository,
                            mockCloudStorageService
                    );
                    String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                    String mockEncodedImageFileName = "test_file.mock";
                    try {
                        Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                                .thenThrow(new CloudStorageClientException());
                    } catch (CorruptedFileException | CloudStorageClientException e) {
                        throw new RuntimeException(e);
                    }
                    Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                    EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                    OrganizationDTORequest dtoWithUpdatedInfo = orgDtoToRequestDtoMapper(orgWithUpdatedInfo, mockEncodedImage);
                    String updatedName = "Updated Name";
                    dtoWithUpdatedInfo.setName(updatedName);
                    // TEST
                    assertThrows(
                            CloudStorageClientException.class,
                            () -> {
                                OrganizationDTO result = organizationService.updateOrganization(dtoWithUpdatedInfo);
                            }
                            , "Expected exception thrown"
                    );
                    Organization orgEntity = organizationsRepository.findById(existingOrgId).orElseThrow();
                    assertNotEquals(dtoWithUpdatedInfo.getName(), orgEntity.getName(), "Process was interrupted and changes were not saved.");
                }

            }

            @Test
            @DisplayName("Image file null")
            void test4() {
                // SETUP
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                        organizationMapper,
                        organizationsRepository,
                        mockCloudStorageService
                );
                Organization orgWithUpdatedInfo = organizationsRepository.findById(existingOrgId).orElseThrow();
                EncodedImageDTO mockEncodedImage = null;
                OrganizationDTORequest dtoWithUpdatedInfo = orgDtoToRequestDtoMapper(orgWithUpdatedInfo, mockEncodedImage);
                String updatedName = "Updated Name";
                dtoWithUpdatedInfo.setName(updatedName);
                // TEST
                assertDoesNotThrow(
                        () -> {
                            OrganizationDTO result = organizationService.updateOrganization(dtoWithUpdatedInfo);
                            assertNotNull(result, "Result object is not null.");
                            assertEquals(updatedName, result.getName(), "Attribute has expected updated value.");
                            assertEquals(orgWithUpdatedInfo.getImage(), result.getImage(), "The image attribute was not updated.");
                        }
                        , "The service did not throw any exception."
                );
                try {
                    Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(Mockito.any(), Mockito.any());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        @Test
        @DisplayName("Organization not found")
        void test5() {
            // SETUP
            CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
            OrganizationServiceImpl organizationService = new OrganizationServiceImpl(
                    organizationMapper,
                    organizationsRepository,
                    mockCloudStorageService
            );
            Organization orgWithNonExistingId = generateMockOrganization(666);
            orgWithNonExistingId.setId("NonExistingId");
            OrganizationDTORequest dtoWithNonExistingId = orgDtoToRequestDtoMapper(orgWithNonExistingId, null);
            // TEST
            assertThrows(RuntimeException.class,
                    () -> {
                        OrganizationDTO result = organizationService.updateOrganization(dtoWithNonExistingId);
                    }
                    , "Expected exception thrown."
            );
            try {
                Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(Mockito.any(), Mockito.any());
            } catch (CorruptedFileException | CloudStorageClientException e) {
                throw new RuntimeException(e);
            }
        }

    }

    static Organization generateMockOrganization(int indexStamp) {
        Organization org = new Organization();
        org.setName("Organization name " + indexStamp);
        org.setWelcomeText("Welcome text " + indexStamp);
        org.setPhone(1160112988 + indexStamp);
        org.setUrlInstagram("UrlInstagram " + indexStamp);
        org.setUrlFacebook("UrlFacebook " + indexStamp);
        org.setEmail("email" + indexStamp + "@mockmail.mock");
        org.setImage("imageUrl " + indexStamp);
        org.setAddress("Address " + indexStamp);
        org.setAboutUsText("AboutUsText " + indexStamp);
        return org;
    }

    static OrganizationDTORequest orgDtoToRequestDtoMapper(Organization organization, EncodedImageDTO encodedImage) {
        OrganizationDTORequest organizationDTORequest = new OrganizationDTORequest();
        organizationDTORequest.setId(organization.getId());
        organizationDTORequest.setName(organization.getName());
        organizationDTORequest.setImage(organization.getImage());
        organizationDTORequest.setPhone(organization.getPhone());
        organizationDTORequest.setAddress(organization.getAddress());
        organizationDTORequest.setEmail(organization.getEmail());
        organizationDTORequest.setWelcomeText(organization.getWelcomeText());
        organizationDTORequest.setAboutUsText(organization.getAboutUsText());
        organizationDTORequest.setUrlFacebook(organization.getUrlFacebook());
        organizationDTORequest.setUrlInstagram(organization.getUrlInstagram());
        organizationDTORequest.setUrlLinkedin(organization.getUrlLinkedin());
        organizationDTORequest.setEncoded_image(encodedImage);
        return organizationDTORequest;
    }

}
