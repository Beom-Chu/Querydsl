package com.kbs.querydsl.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.Team;

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

  @Disabled
  @Test
  public void querydslTest() {
    Member member = new Member("member1", 10);
    memberJpaRepository.save(member);
    
    List<Member> result1 = memberJpaRepository.findAll_Querydsl();
    assertThat(result1).containsExactly(member);
    
    List<Member> result2 = memberJpaRepository.findByusername_Querydsl("member1");
    assertThat(result2).containsExactly(member);
  }
  
  /**
   * 동적쿼리 - Builder 사용
   */
  @Test
  public void searchTest() {
    
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    
    em.persist(teamA);
    em.persist(teamB);
    
    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
    
    MemberSearchCond cond = new MemberSearchCond();
    cond.setAgeGoe(35);
    cond.setAgeLoe(40);
    cond.setTeamName("teamB");
    
    List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(cond);
    
    assertThat(result).extracting("username").containsExactly("member4");
    
  }
}
