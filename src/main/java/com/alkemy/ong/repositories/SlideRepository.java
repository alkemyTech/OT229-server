package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.entities.SlidesEntity;

import java.util.List;

@Repository
public interface SlideRepository extends JpaRepository<SlidesEntity, String> {

    public List<SlidesEntity> findByOrganizationIdOrderBySlideOrder (String organizationId);
    public List<SlidesEntity> findBySlideOrder(Integer order);

    public List<SlidesEntity> findAllByOrderBySlideOrderAsc();

    @Query(value = "SELECT MAX(s.slideOrder) FROM SlidesEntity s WHERE s.organizationId = :organizationId")
    Integer getLastOrder(@Param("organizationId") String organizationId);
}
