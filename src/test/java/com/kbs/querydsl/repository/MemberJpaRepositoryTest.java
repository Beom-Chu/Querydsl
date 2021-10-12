package com.kbs.querydsl.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.entity.Member;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

  @Autowired
  EntityManager em;
  
  @Autowired
  MemberJpaRepository memberJpaRepository;
  
  @Disabled
  @Test
  void basicTest() {
    
    Member member = new Member("member1", 10);
    memberJpaRepository.save(member);
    
    
    Member findMember = memberJpaRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);
    
    
    List<Member> result1 = memberJpaRepository.findAll();
    assertThat(result1).containsExactly(member);
    
    
    List<Member> result2 = memberJpaRepository.findByUsername("member1");
    assertThat(result2).containsExactly(member);
    
  }

  
  @Test
  public void querydslTest() {
    Member member = new Member("member1", 10);
    memberJpaRepository.save(member);
    
    List<Member> result1 = memberJpaRepository.findAll_Querydsl();
    assertThat(result1).containsExactly(member);
    
    List<Member> result2 = memberJpaRepository.findByusername_Querydsl("member1");
    assertThat(result2).containsExactly(member);
  }
}
