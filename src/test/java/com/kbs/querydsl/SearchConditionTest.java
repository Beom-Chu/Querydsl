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
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
public class SearchConditionTest {

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
    
    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1")
            .and(member.age.eq(10)))
        .fetchOne();
        
    assertThat(findMember.getUsername()).isEqualTo("member1");
    
    List<Member> fetch = queryFactory
    .selectFrom(member)
    .where(
         member.username.eq("member1")        //username = 'member1'
        ,member.username.ne("member1")        //username != 'member1'
        ,member.username.eq("member1").not()  //username != 'member1'
        
        ,member.username.isNotNull()  //username is not null
        
        ,member.age.in(10,20)       //age in (10,20)
        ,member.age.notIn(10,20)    //age not in (10,20)
        ,member.age.between(10, 30) //age between 10 and 30
        
        ,member.age.goe(30) //age >= 30
        ,member.age.gt(30)  //age > 30
        ,member.age.loe(30) //age <= 30
        ,member.age.lt(30)  //age < 30
        
        ,member.username.like("member%")      //username like 'member%'
        ,member.username.contains("member")   //username like '%member%'
        ,member.username.startsWith("member") //username like 'member%'

        ).fetch();
    // 조건절을 .and() 대신에 , 콤마로 사용 가능
    
    assertThat(fetch.size()).isEqualTo(0);
  }
}
