package com.kbs.querydsl.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.kbs.querydsl.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, QuerydslPredicateExecutor<Member> {

  List<Member> findByUsername(String username);
  
  List<Member> findByTeamId(Long id);
  
  List<Member> findByTeamName(String teamName);
}
