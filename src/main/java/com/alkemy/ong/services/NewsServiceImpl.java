package com.alkemy.ong.services;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.entities.News;
import com.alkemy.ong.mappers.NewsMapper;
import com.alkemy.ong.repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl implements NewsService{
    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private NewsRepository newsRepository;

    @Override
    public NewsDTO save(NewsDTO news) {
        News entity = newsMapper.newsDTO2Entity(news);
        News newsSaved = newsRepository.save(entity);
        NewsDTO result = newsMapper.newsEntity2DTO(newsSaved);
        return result;
    }
}
