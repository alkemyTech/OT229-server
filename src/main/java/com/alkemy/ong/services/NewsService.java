package com.alkemy.ong.services;

import com.alkemy.ong.dto.NewsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface NewsService {
  NewsDTO save (MultipartFile file, NewsDTO news) throws IOException;

}
