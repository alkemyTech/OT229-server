package com.alkemy.ong.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.entities.SlidesEntity;

@Repository
public interface SlideRepository extends JpaRepository<SlidesEntity, String> {

  SlideRepository findBySlideOrder(int order);
  SlideRepository findTopByOrderSlideBySlideOrderDesc();
  SlideRepository findByIdSlideRepository(String id);

}
