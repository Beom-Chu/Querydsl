package com.kbs.querydsl.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.Team;

@SpringBootTest
@ActiveProfiles(value = "test")
@Transactional
class MemberRepositoryTest {

  @Autowired
  EntityManager em;
  
  @Autowired
  MemberRepository memberRepository;
  
  @Test
  @Disabled
  public void basicTest() {
    
    Member member = new Member("member1", 10);
    memberRepository.save(member);
    
    Member findMember = memberRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);
    
    List<Member> result1 = memberRepository.findAll();
    assertThat(result1).containsExactly(member);
    
    List<Member> result2 = memberRepository.findByUsername("member1");
    assertThat(result2).containsExactly(member);
    
  }

  @Test
  @Disabled
  public void test2() {
    
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
    
    em.flush();
    em.clear();
    
//    List<Member> result = memberRepository.findByTeamId(1L);
    List<Member> result = memberRepository.findByTeamName("teamA");
    
    
    for (Member member : result) {
      System.out.println(member);
    }
  }
  
  
  /**
   * 커스텀 리포지토리 동작 테스트
   */
  @Disabled
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
    
    List<MemberTeamDto> result = memberRepository.search(cond);
    assertThat(result).extracting("username").containsExactly("member4");
    
  }
  
  
  /**
   * 페이징 활용 테스트 - simple
   */
  @Test
  public void searchPageSimpleTest() {
    
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

    PageRequest pageRequest = PageRequest.of(0, 3);
    
    Page<MemberTeamDto> result = memberRepository.searchPageSimple(cond, pageRequest);

    assertThat(result.getSize()).isEqualTo(3);
    assertThat(result.getContent()).extracting("username").containsExactly("member1","member2","member3");
    
  }
}
