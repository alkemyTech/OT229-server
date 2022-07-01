package com.alkemy.ong.services;

import com.alkemy.ong.dto.NewsDTO;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

public interface NewsService {
  NewsDTO save (MultipartFile file, NewsDTO news) throws IOException;

  NewsDTO findById(String id);

  /**
   * Deletes a news entry from the persistent storage.
   *
   * @param id  the id of the News object to be deleted.
   * @return  the DTO of the deleted object.
   * @throws EntityNotFoundException  if an entity with the provided id can't be found.
   * @throws IOException  if there was a problem with the cloud storage service while attempting to delete the associated image.
   */
  NewsDTO deleteNews(String id) throws EntityNotFoundException, IOException;

}
