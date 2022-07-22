package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.H2Configuration;
import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.dto.ActivityDTORequest;
import com.alkemy.ong.dto.EncodedImageDTO;
import com.alkemy.ong.entities.ActivityEntity;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.mappers.ActivityMapper;
import com.alkemy.ong.repositories.ActivityRepository;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {H2Configuration.class}, loader = AnnotationConfigContextLoader.class)
public class ActivitiesServiceImplTest {
    private static ActivityMapper activityMapper = new ActivityMapper();

    @Autowired
    private ActivityRepository activityRepository;

    private static String existingActivityByName = "";
    private static final int numberOfMockOrgs = 5;

    @BeforeEach
    @Transactional
    void populateDatabase() {
        ActivityEntity activity = new ActivityEntity();
        for (int i = 1; i <= numberOfMockOrgs; i++) {
            activity = activityRepository.save(generateMockActivity(i));
        }
        existingActivityByName = activity.getName();
    }

    @AfterEach
    @Transactional
    void emptyDatabase() {
        activityRepository.deleteAll();
    }

    @Nested
    class saveActivity{
        @Nested
        class WithMultiPartFile{
            @Test
            @DisplayName("Successful save")
            void test1(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Test name";
                ActivityDTO activityDTO = generateANewActivityDTO(nameNewActivity);

                assertDoesNotThrow(
                    () -> {
                        ActivityDTO result = activitiesService.save(mockImageFile, activityDTO);
                        assertNotNull(result, "Result object is not null.");
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
            @DisplayName("Name already exist")
            void test2(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Activity 2";
                ActivityDTO activityDTO = generateANewActivityDTO(nameNewActivity);

                assertThrows(
                    ActivityNamePresentException.class,
                    () -> {
                        ActivityDTO result = activitiesService.save(mockImageFile, activityDTO);
                    }
                        ,"Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Corrupted file")
            void test3(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());

                try {
                    Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenThrow(new CorruptedFileException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Test name";
                ActivityDTO activityDTO = generateANewActivityDTO(nameNewActivity);

                assertThrows(
                    CorruptedFileException.class,
                    () -> {
                        ActivityDTO result = activitiesService.save(mockImageFile, activityDTO);
                    }
                    ,"Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Image null")
            void test4(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                String nameNewActivity = "Test name";
                ActivityDTO activityDTO = generateANewActivityDTO(nameNewActivity);

                assertDoesNotThrow(
                        () -> {
                            ActivityDTO result = activitiesService.save(null, activityDTO);
                            assertNotNull(result, "Result object is not null.");
                            assertEquals(activityDTO.getImage(), result.getImage(), "The image attribute was not updated.");
                        }
                        , "The service did not throw any exception."
                );

                try {
                    Mockito.verify(mockCloudStorageService, Mockito.never()).uploadFile(Mockito.any());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("S3 Service problem")
            void test5(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                MultipartFile mockImageFile = new MockMultipartFile("test_file.mock", "MockContent".getBytes());

                try {
                    Mockito.when(mockCloudStorageService.uploadFile(mockImageFile)).thenThrow(new CloudStorageClientException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Test name";
                ActivityDTO activityDTO = generateANewActivityDTO(nameNewActivity);

                assertThrows(
                        CloudStorageClientException.class,
                        () -> {
                            ActivityDTO result = activitiesService.save(mockImageFile, activityDTO);
                        }
                        , "Expected exception thrown"
                );
            }
        }

        @Nested
        class WithImageBase64Encoded{
            @Test
            @DisplayName("Successful save")
            void test1(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Test name";
                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                ActivityDTORequest activityDTORequest = generateANewActivityDTORequest(nameNewActivity, mockEncodedImage);

                assertDoesNotThrow(
                    () -> {
                        ActivityDTO result = activitiesService.save(activityDTORequest);
                        assertNotNull(result, "Result object is not null.");
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
            @DisplayName("Name already exist")
            void test2(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                String mockUploadedFileUrl = "www.mockurl.mock/test_file.mock";

                try {
                    Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenReturn(mockUploadedFileUrl);
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Activity 2";
                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                ActivityDTORequest activityDTORequest = generateANewActivityDTORequest(nameNewActivity, mockEncodedImage);

                assertThrows(
                    ActivityNamePresentException.class,
                    () -> {
                        ActivityDTO result = activitiesService.save(activityDTORequest);
                    }
                    ,"Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Corrupted file")
            void test3(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";

                try {
                    Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenThrow(new CorruptedFileException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Test name";
                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                ActivityDTORequest activityDTORequest = generateANewActivityDTORequest(nameNewActivity, mockEncodedImage);

                assertThrows(
                        CorruptedFileException.class,
                        () -> {
                            ActivityDTO result = activitiesService.save(activityDTORequest);
                        }
                        ,"Expected exception thrown"
                );
            }

            @Test
            @DisplayName("Image null")
            void test4(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                String nameNewActivity = "Test name";
                EncodedImageDTO mockEncodedImage = null;
                ActivityDTORequest activityDTORequest = generateANewActivityDTORequest(nameNewActivity, mockEncodedImage);

                assertDoesNotThrow(
                    () -> {
                        ActivityDTO result = activitiesService.save(activityDTORequest);
                        assertNotNull(result, "Result object is not null.");
                        assertEquals(activityDTORequest.getImage(), result.getImage(), "The image attribute was accurately updated.");
                    }
                    , "The service did not throw any exception."
                );

                try {
                    Mockito.verify(mockCloudStorageService, Mockito.never()).uploadBase64File(Mockito.any(), Mockito.any());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }
            }

            @Test
            @DisplayName("S3 Service problem")
            void test5(){
                CloudStorageService mockCloudStorageService = Mockito.mock(CloudStorageService.class);
                ActivitiesServiceImpl activitiesService = new ActivitiesServiceImpl(activityMapper, activityRepository, mockCloudStorageService);

                String mockEncodedImageFileContent = "MockEncodedImageFileContent";
                String mockEncodedImageFileName = "test_file.mock";
                try {
                    Mockito.when(mockCloudStorageService.uploadBase64File(mockEncodedImageFileContent, mockEncodedImageFileName))
                            .thenThrow(new CloudStorageClientException());
                } catch (CorruptedFileException | CloudStorageClientException e) {
                    throw new RuntimeException(e);
                }

                String nameNewActivity = "Test name";
                EncodedImageDTO mockEncodedImage = new EncodedImageDTO(mockEncodedImageFileContent, mockEncodedImageFileName);
                ActivityDTORequest activityDTORequest = generateANewActivityDTORequest(nameNewActivity, mockEncodedImage);

                assertThrows(
                        CloudStorageClientException.class,
                        () -> {
                            ActivityDTO result = activitiesService.save(activityDTORequest);
                        }
                        ,"Expected exception thrown"
                );
            }
        }
    }

    private static ActivityEntity generateMockActivity(int indedxStamp){
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setId("Activity ID " + indedxStamp);
        activityEntity.setName("Activity " + indedxStamp);
        activityEntity.setContent("Activity content " + indedxStamp);
        activityEntity.setImage("image.jpg " + indedxStamp);
        activityEntity.setTimeStamps(new Date());

        return activityEntity;
    }

    private static ActivityDTO generateANewActivityDTO(String name){
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setName(name);
        activityDTO.setContent("Test save content");
        activityDTO.setImage("testSave.jpg");

        return activityDTO;
    }

    private static ActivityDTORequest generateANewActivityDTORequest(String name, EncodedImageDTO encodedImage){
        ActivityDTORequest activityDTORequest = new ActivityDTORequest();
        activityDTORequest.setName(name);
        activityDTORequest.setContent("Test save content");
        activityDTORequest.setEncoded_image(encodedImage);
        activityDTORequest.setImage("testSave.jpg");

        return activityDTORequest;
    }
}
