package com.alkemy.ong.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.entities.ActivityEntity;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, String> {

}

