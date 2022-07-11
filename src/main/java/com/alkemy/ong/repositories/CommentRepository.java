package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity,String> {

    List<CommentEntity> findAllByNewsIdOrderByCreateDateAsc(String newsId);
    List<CommentEntity> findAllByOrderByCreateDateDesc();
}
