package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersRepository extends JpaRepository<Members, String> {

}
