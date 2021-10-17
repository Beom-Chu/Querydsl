package com.kbs.querydsl.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kbs.querydsl.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

  List<Member> findByUsername(String username);
  
  List<Member> findByTeamId(Long id);
  
  List<Member> findByTeamName(String teamName);
}
