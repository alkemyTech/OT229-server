package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.dto.ReducedOrganizationDTO;
import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.mappers.OrganizationMapper;
import com.alkemy.ong.repositories.OrganizationsRepository;
import com.alkemy.ong.services.CloudStorageService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
class OrganizationServiceImplTest {

    private final OrganizationMapper organizationMapper = new OrganizationMapper();
    @Autowired
    private OrganizationsRepository organizationsRepository;

    private static String existingOrgId = "";
    private static final int numberOfMockOrgs = 10;

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
                assertNotNull(resultList.get(i).getName(), "Name attribute from result list's object is not null");
                assertTrue(resultList.get(i).getName().contains("name"), "Attribute has expected mock value." );
            }
        }

        @Test
        @DisplayName("Empty list returned")
        void test2() {

        }

    }

    @Nested
    class GetByIdTest {

        @Test
        @DisplayName("Organization found")
        void test1() {

        }

        @Test
        @DisplayName("Organization not found")
        void test2() {

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

                }

                @Test
                @DisplayName("Corrupted file")
                void test2() {

                }

                @Test
                @DisplayName("S3 Service problem")
                void test3() {

                }

            }

            @Test
            @DisplayName("Image file null")
            void test4() {

            }

        }

        @Test
        @DisplayName("Organization not found")
        void test5() {

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

                }

                @Test
                @DisplayName("Corrupted file")
                void test2() {

                }

                @Test
                @DisplayName("S3 Service problem")
                void test3() {

                }

            }

            @Test
            @DisplayName("Image file null")
            void test4() {

            }

        }

        @Test
        @DisplayName("Organization not found")
        void test5() {

        }

    }

    @Nested
    class TestUpdateOrganizationTest {

        @Test
        @DisplayName("Valid case")
        void test1() {

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.services.impl.OrganizationServiceImplTest#generateDtosWithMissingMandatoryAttributes")
        @DisplayName("Mandatory attributes missing")
        void test2(OrganizationDTO updatedOrganizationInfo) {

        }

        @Test
        @DisplayName("Invalid email format")
        void test3() {

        }

    }

    static List<OrganizationDTO> generateDtosWithMissingMandatoryAttributes() {
        return Collections.singletonList(new OrganizationDTO());
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

}