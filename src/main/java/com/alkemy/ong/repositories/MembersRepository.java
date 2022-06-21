package com.alkemy.ong.repositories;

import com.alkemy.ong.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersRepository extends JpaRepository<Member, String> {

}
