package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News,String> {
}
