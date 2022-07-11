package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.dto.DatedNewsDTO;
import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.entities.Category;
import com.alkemy.ong.entities.News;
import com.alkemy.ong.exception.*;
import com.alkemy.ong.mappers.CategoryMapper;
import com.alkemy.ong.mappers.NewsMapper;
import com.alkemy.ong.mappers.PageResultResponseBuilder;
import com.alkemy.ong.repositories.NewsRepository;
import com.alkemy.ong.services.CategoriesService;
import com.alkemy.ong.services.CategoryEntityProvider;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.NewsService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

  @Autowired
  private NewsMapper newsMapper;
  @Autowired
  private NewsRepository newsRepository;
  @Autowired
  private CloudStorageService cloudStorageService;
  @Autowired
  private CategoriesService categoriesService;
  @Autowired
  private CategoryEntityProvider categoryEntityProvider;
  @Autowired
  private CategoryMapper categoryMapper;

  @Transactional
  @Override
  public NewsDTO save(MultipartFile file, NewsDTO news) throws CloudStorageClientException, CorruptedFileException {

    String imageUrl=(cloudStorageService.uploadFile(file));
    news.setImage(imageUrl);
    News entity = this.newsMapper.newsDTO2Entity(news);
    News newsSaved = this.newsRepository.save(entity);
    return this.newsMapper.newsEntity2DTO(newsSaved);
  }

  @Override
  public NewsDTO findById(String id) {
    Optional<News> news = newsRepository.findById(id);
    if(!news.isPresent()){
      throw new RuntimeException("Not found news");
    }
    return newsMapper.newsEntity2DTO(news.get());
  }

  @Override
  public boolean existNew(String id) {
    return newsRepository.existsById(id);
  }

  @Transactional
  @Override
  public NewsDTO deleteNews(String id) throws EntityNotFoundException, CloudStorageClientException {
    News newsToDelete = this.newsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("News with the provided id not found."));
    try {
      this.deleteNewsImageFromCloudStorage(newsToDelete);
    } catch (FileNotFoundOnCloudException e) {
      // If the file didn't exist, nothing needs to be done.
    }
    NewsDTO newsDTO = this.newsMapper.newsEntity2DTO(newsToDelete);
    this.newsRepository.delete(newsToDelete);
    return newsDTO;
  }

  @Transactional
  @Override
  public NewsDTO updateNews(String id, MultipartFile imageFile, NewsDTO updatedNews) throws EntityNotFoundException, IllegalArgumentException, CloudStorageClientException, CorruptedFileException {
    News newsToUpdate = this.newsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("News with the provided id not found."));
    this.newsMapper.UpdateNewsInstance(newsToUpdate, updatedNews);
    this.updateNewsCategory(newsToUpdate, updatedNews.getCategory());
    // If the News is not going to be update with a new image, then this attribute should have the current image url.
    String updatedImageUrl = updatedNews.getImage();
    if (imageFile != null && !imageFile.isEmpty()) {
      if (updatedImageUrl != null) {
        try {
          this.cloudStorageService.deleteFileFromS3Bucket(updatedImageUrl);
        } catch (FileNotFoundOnCloudException e) {
          // If the file didn't exist, nothing needs to be done.
        }
      }
      updatedImageUrl = cloudStorageService.uploadFile(imageFile);
    }
    updatedNews.setImage(updatedImageUrl);
    return this.newsMapper.newsEntity2DTO(newsToUpdate);
  }

  @Override
  public PageResultResponse<DatedNewsDTO> getAllNews(int pageNumber) throws PageIndexOutOfBoundsException {
    if (pageNumber < 0) {
      throw new PageIndexOutOfBoundsException("Page number must be positive.");
    }
    Pageable pageRequest = PageRequest.of(
            pageNumber,
            GlobalConstants.GLOBAL_PAGE_SIZE,
            Sort.by(GlobalConstants.NEWS_SORT_ATTRIBUTE)
    );
    Page<News> springDataResultPage = this.newsRepository.findAll(pageRequest);
    return new PageResultResponseBuilder<News, DatedNewsDTO>()
            .from(springDataResultPage)
            .mapWith(this.newsMapper::newsEntity2DatedDTO)
            .build();
  }

  /**
   * Obtains and sets the Category instance for a News entity that's going to be updated or created.
   *
   * To be updated or created, the News' category attribute must be an entity tracked by JPA so that the correct
   * association can be established on save or update. This retrieves this Category instance if it exists, or it
   * creates it and then returns it.
   *
   * @param updatedNews the News to be updated.
   * @param categoryDTO the updated Category to be attached to the News.
   * @throws IllegalArgumentException if the category must be created but doesn't have a proper name.
   */
  @Transactional
  private void updateNewsCategory(News updatedNews, CategoryDTO categoryDTO) throws IllegalArgumentException {
    Category category = null;
    if (categoryDTO != null) {
      if (categoryDTO.getName() == null || categoryDTO.getName().equals("")) {
        throw new IllegalArgumentException("The Category provided for the News doesn't have a valid name.");
      }
      // If the category exists, retrieves it from the db.
      category = this.categoryEntityProvider.getEntityByName(updatedNews.getCategory().getName())
              // If it's a new category, it should be created.
              .orElseGet(
                      () -> {
                        CategoryDTO newCategoryDTO = this.categoriesService.save(categoryDTO);
                        // This is the return from this lambda function, not the method's return.
                        return this.categoryEntityProvider.getEntityByName(newCategoryDTO.getName()).get();
                      }
              );
    }
    updatedNews.setCategory(category);
  }

  /**
   * Deletes the image referenced by a News entity from the cloud storage.
   *
   * @param news the owner of the image.
   * @throws CloudStorageClientException  if there was a problem with the cloud storage client.
   */
  private void deleteNewsImageFromCloudStorage(News news) throws CloudStorageClientException, FileNotFoundOnCloudException {
    if (news.getImage() != null && !news.getImage().equals("")) {
      try {
        this.cloudStorageService.deleteFileFromS3Bucket(news.getImage());
      } catch (EntityImageProcessingException e) {
        // All exceptions thrown from deleteFileFromS3Bucket() inherit from EntityImageProcessingException,
        // FileNotFoundOnCloudException being one. In the case where it was FileNotFoundOnCloudException, meaning there
        // was no file stored in the S3 service with the provided url, then I don't need to do anything, hence it's
        // excluded from the "if" and it's not thrown again.
        if (!(e instanceof FileNotFoundOnCloudException)) {
          throw e;
        }
      }
    }
  }

}
