package com.alkemy.ong.services;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

public interface NewsService {
  NewsDTO save (MultipartFile file, NewsDTO news) throws CloudStorageClientException, CorruptedFileException;

  NewsDTO findById(String id);

  /**
   * Deletes a news entry from the persistent storage.
   *
   * @param id  the id of the News object to be deleted.
   * @return  the DTO of the deleted object.
   * @throws EntityNotFoundException  if an entity with the provided id can't be found.
   * @throws CloudStorageClientException  if there was a problem with the cloud storage service while attempting to delete the associated image.
   */
  NewsDTO deleteNews(String id) throws EntityNotFoundException, CloudStorageClientException;

  /**
   * Updates a News entry.
   *
   * @param id  the id of the News to be edited.
   * @param image a new image for the News entity, or <code>null</code> if the image is not to be updated.
   * @param updatedNews the DTO with the updated fields of the News. The Entity whole Entity is updated from these values,
   *                    so every field must be present, not just the updated ones.
   * @return  an dto with the info from the updated entity.
   * @throws EntityNotFoundException  if an entity with the provided id can't be found.
   * @throws CorruptedFileException  if there was a problem with the file attached.
   * @throws CloudStorageClientException  if there was a problem with the cloud storage service.
   * @throws IllegalArgumentException if the News' Category to be updated is present but has an invalid name.
   */
  NewsDTO updateNews(String id, MultipartFile image, NewsDTO updatedNews) throws EntityNotFoundException, IllegalArgumentException, CloudStorageClientException, CorruptedFileException;

  /**
   * Performs a paginated search for all the News entries in the database and returns them alongside the urls
   * to get the previous and next page results.
   *
   * <p>Page size and sorting criteria are read from global constants.
   * @param pageNumber  the index of the page to be retrieved.
   * @return the list of News. If the provided index exceeds the last existing index, an empty list will
   *          be returned, and the previous page url attribute will point to the last available page.
   * @throws PageIndexOutOfBoundsException  if the index is not a positive integer.
   */
  PageResultResponse<NewsDTO> getAllNews(int pageNumber) throws PageIndexOutOfBoundsException;

}
