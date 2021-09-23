package com.kbs.querydsl;

import static com.kbs.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
public class SearchResultTest {

  @PersistenceContext
  private EntityManager em;
  
  //JPAQueryFactory를 필드로
  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    
    queryFactory = new JPAQueryFactory(em);
    
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
  }
  
  @Test
  public void search() {
    
    //List
    List<Member> fetch = queryFactory
      .selectFrom(member)
      .fetch();
    
    //단건
    Member fetchOne = queryFactory
      .selectFrom(member)
      .fetchOne();
    
    //처음 한 건 조회
    Member fetchFirst = queryFactory
      .selectFrom(member)
      .fetchFirst();
    
    //페이징에서 사용
    QueryResults<Member> fetchResults = queryFactory
      .selectFrom(member)
      .fetchResults();
    
    //count 쿼리로 변경
    long fetchCount = queryFactory
      .selectFrom(member)
      .fetchCount();
  }
}
