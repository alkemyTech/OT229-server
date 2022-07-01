package com.alkemy.ong.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.entities.ActivityEntity;

import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, String> {

    @Query(
            value = "select * from activity a where a.name = ? limit 1",
            nativeQuery = true
    )
    Optional<ActivityEntity> findByName(String name);
}

