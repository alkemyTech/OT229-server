package com.alkemy.ong.repositories;
import com.alkemy.ong.entities.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial,String> {

}