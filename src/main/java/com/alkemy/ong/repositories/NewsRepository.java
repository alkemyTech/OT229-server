package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News,String> {

    Optional<News> findById(String id);

    @Modifying
    @Query("update News n set n.category = null where n.category.id = :id")
    void detachCategory(@Param(value ="id")String id);

    Page<News> findAll(Pageable pageRequest);

}
