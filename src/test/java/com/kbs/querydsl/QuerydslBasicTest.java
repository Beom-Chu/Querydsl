package com.kbs.querydsl;

//기본 인스턴스를 static import와 함께 사용
import static com.kbs.querydsl.entity.QMember.member;
import static com.kbs.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.QMember;
import com.kbs.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
  @Disabled
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
  
  @Disabled
  @Test
  public void paging1() {
    List<Member> result = queryFactory
      .selectFrom(member)
      .orderBy(member.username.desc())
      .offset(0)
      .limit(2)
      .fetch();
    
    assertThat(result.size()).isEqualTo(2);
  }
  
  @Disabled
  @Test
  public void paging2() {
    QueryResults<Member> queryResults = queryFactory
      .selectFrom(member)
      .orderBy(member.username.desc())
      .offset(1)
      .limit(2)
      .fetchResults();
    
    assertThat(queryResults.getTotal()).isEqualTo(4);
    assertThat(queryResults.getLimit()).isEqualTo(2);
    assertThat(queryResults.getOffset()).isEqualTo(1);
    assertThat(queryResults.getResults().size()).isEqualTo(2);
  }
  
  
  /**
   * JPQL
   * select
   * COUNT(m), //회원수
   * SUM(m.age), //나이 합
   * AVG(m.age), //평균 나이
   * MAX(m.age), //최대 나이
   * MIN(m.age) //최소 나이
   * from Member m
   */
  @Disabled
  @Test
  public void aggregation() {
    
    List<Tuple> result = queryFactory
        .select(member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min())
        .from(member)
        .fetch();
    
    Tuple tuple = result.get(0);
    
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
  }
  
  
  /**
   * 팀의 이름과 각 팀의 평균 연령을 구해라.
   */
  @Disabled
  @Test
  public void group() {
    
    List<Tuple> result = queryFactory
      .select(team.name, member.age.avg())
      .from(member)
      .groupBy(team.name)
      .fetch();
   
    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);
    
    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);
    
    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    
  }
  
  /**
   * 팀 A에 소속된 모든 회원
   */
  @Disabled
  @Test
  public void join() {
    
    List<Member> result = queryFactory
      .selectFrom(member)
      .join(member.team, team)
//      .innerJoin(member.team,team)
//      .leftJoin(member.team, team)
//      .rightJoin(member.team, team)
      .where(team.name.eq("teamA"))
      .fetch();
    
    //join(), innerJoin() : 내부조인
    //leftJoin() : left outer join
    //rightJoin() : right outer join
    
    assertThat(result)
      .extracting("username")
      .containsExactly("member1","member2");
  }
  
  
  /**
   * 세타 조인(연관관계가 없는 필드로 조인)
   * 회원의 이름이 팀 이름과 같은 회원 조회
   */
  @Disabled
  @Test
//  @Rollback(value = false)
  public void theta_join() {
    
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    
    List<Member> result = queryFactory
      .select(member)
      .from(member, team)
      .where(member.username.eq(team.name))
      .fetch();
    
    assertThat(result)
      .extracting("username")
      .containsExactly("teamA", "teamB");
  }
  
  
  /**
   * 1. 조인 대상 필터링
   * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
   * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
   * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
   */
  @Test
  public void join_on_filtering() {
    
    List<Tuple> result = queryFactory
      .select(member, team)
      .from(member)
      .leftJoin(member.team, team).on(team.name.eq("teamA"))
      .fetch();
    
    for(Tuple tuple : result) {
      System.out.println("join_on_filtering : "+tuple);
    }
  }
  
  
  /**
   * 2. 연관관계 없는 엔티티 외부 조인
   * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
   * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
   * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
   */
  @Test
  public void join_on_no_relation() {
    
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    
    List<Tuple> result = queryFactory
      .select(member, team)
      .from(member)
      .leftJoin(team).on(member.username.eq(team.name))
      .fetch();
    
    for(Tuple tuple : result) {
      System.out.println("join_on_no_relation : "+tuple);
    }
  }
}
