package com.alkemy.ong.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.entities.SlidesEntity;

import java.util.List;

@Repository
public interface SlideRepository extends JpaRepository<SlidesEntity, String> {

    public List<SlidesEntity> findByOrganizationId (String organizationId);
    public List<SlidesEntity> findBySlideOrder(Integer order);
}
