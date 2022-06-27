
package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query(
            value = "select * from categories c where c.name = ? limit 1",
            nativeQuery = true
    )
    Optional<Category> findByName(String name);
}
