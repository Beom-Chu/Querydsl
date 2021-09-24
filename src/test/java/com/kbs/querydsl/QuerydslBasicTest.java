package com.kbs.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.plaf.metal.MetalMenuBarUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.QMember;
import com.kbs.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

//기본 인스턴스를 static import와 함께 사용
import static com.kbs.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

  @PersistenceContext
  private EntityManager em;
  
  JPAQueryFactory queryFactory; //JPAQueryFactory를 필드로
  
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

  @Disabled
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

  @Disabled
  @Test
  public void startQuerydsl() {
    
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    QMember m = new QMember("m");
    Member findMember = queryFactory
        .select(m)
        .from(m)
        .where(m.username.eq("member1"))// 파라미터 바인딩 처리
        .fetchOne();
    
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Disabled
  @Test
  public void startQuerydsl2() {
    
    //JPAQueryFactory를 필드로
    
    QMember m = new QMember("m");
    Member findMember = queryFactory  
        .select(m)
        .from(m)
        .where(m.username.eq("member1"))
        .fetchOne();
    
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }
  
  @Disabled
  @Test
  public void startQuerydsl3() {

//    QMember qMember = new QMember("m"); //별칭 직접 지정
//    QMember qMember = QMember.member; //기본 인스턴스 사용
    
    //기본 인스턴스를 static import와 함께 사용
    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();
    
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }
  
  @Disabled
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
  
  @Disabled
  @Test
  public void searchResultTest() {
    
    //List
    List<Member> fetch = queryFactory
      .selectFrom(member)
      .fetch();
    
    //단건
    Member fetchOne = queryFactory
      .selectFrom(member)
      .where(member.id.eq(1L))
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
  
  /**
   * 회원 정렬 순서
   * 1. 회원 나이 내림차순(desc)
   * 2. 회원 이름 올림차순(asc)
   * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
   */
  @Test
  public void sort() {
    
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));
    
    List<Member> result = queryFactory
      .selectFrom(member)
      .where(member.age.eq(100))
      .orderBy(member.age.desc(), member.username.asc().nullsLast())
      .fetch();
    
    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);
    
    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
  }
}
