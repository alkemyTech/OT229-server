package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.entities.News;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.mappers.NewsMapper;
import com.alkemy.ong.repositories.NewsRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.NewsService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

  @Autowired
  private NewsMapper newsMapper;
  @Autowired
  private NewsRepository newsRepository;
  @Autowired
  private CloudStorageService cloudStorageService;

  @Transactional
  @Override
  public NewsDTO save(MultipartFile file, NewsDTO news) throws IOException {

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

  @Transactional
  @Override
  public NewsDTO deleteNews(String id) throws EntityNotFoundException, IOException {
    News newsToDelete = this.newsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("News with the provided id not found."));
    this.deleteNewsImageFromCloudStorage(newsToDelete);
    NewsDTO newsDTO = this.newsMapper.newsEntity2DTO(newsToDelete);
    this.newsRepository.delete(newsToDelete);
    return newsDTO;
  }

  /**
   * Deletes the image referenced by a News entity from the cloud storage.
   *
   * @param news the owner of the image.
   * @throws IOException  if there was a problem with the cloud storage client.
   */
  private void deleteNewsImageFromCloudStorage(News news) throws IOException {
    if (news.getImage() != null && !news.getImage().equals("")) {
      try {
        this.cloudStorageService.deleteFileFromS3Bucket(news.getImage());
      } catch (IOException e) {
        // All exceptions thrown from deleteFileFromS3Bucket() inherit from IOException, FileNotFoundException being one.
        // In the case where it was FileNotFoundException, meaning there was no file stored in the S3 service with the
        // provided url, then I don't need to do anything, hence it's excluded from the "if" and it's not thrown again.
        if (!(e instanceof FileNotFoundException)) {
          throw e;
        }
      }
    }
  }

}
