
package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query(
            value = "select * from categories c where c.name = ? limit 1",
            nativeQuery = true
    )
    Optional<Category> findByName(String name);
    
    List<Category> findAllByOrderByName();

    Page<Category> findAll(Pageable pageable);

}
