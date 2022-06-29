package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.entities.News;
import com.alkemy.ong.mappers.NewsMapper;
import com.alkemy.ong.repositories.NewsRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.NewsService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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

}
