package com.kbs.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.QMember;
import com.kbs.querydsl.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

  @PersistenceContext
  private EntityManager em;
  
  JPAQueryFactory queryFactory; //JPAQueryFactory를 필드로
  
  @BeforeEach
  public void before() {
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
  }

  @Test
  @DisplayName("JPQL test")
  public void startJPQL() {
    
    String qlString = 
          "select m from Member m "
        + "where m.username = :username";
    
    Member findMember = em.createQuery(qlString,Member.class)
        .setParameter("username", "member1")
        .getSingleResult();
    
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  @DisplayName("Querydsl test")
  public void startQuerydsl() {
    
//    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    queryFactory = new JPAQueryFactory(em);
    
//    QMember member = new QMember("m"); //별칭 직접 지정 방식
    QMember member = QMember.member;  //기본 인스턴스 사용
    
    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();
    
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }
}
