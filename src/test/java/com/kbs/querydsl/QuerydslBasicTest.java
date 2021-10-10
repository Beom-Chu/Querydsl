package com.kbs.querydsl;

//기본 인스턴스를 static import와 함께 사용
import static com.kbs.querydsl.entity.QMember.member;
import static com.kbs.querydsl.entity.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.dto.MemberDto;
import com.kbs.querydsl.dto.QMemberDto;
import com.kbs.querydsl.dto.UserDto;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.entity.QMember;
import com.kbs.querydsl.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

  @PersistenceContext
  private EntityManager em;
  
  @PersistenceUnit
  private EntityManagerFactory emf;
  
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
  @Disabled
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
  @Disabled
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
  
  
  
  /**
   * 페치조인 미적용
   */
  @Disabled
  @Test
  public void fetchJoinNo() {
    em.flush();
    em.clear();
    
    Member result = queryFactory
      .selectFrom(member)
      .where(member.username.eq("member1"))
      .fetchOne();
    
    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(result.getTeam());
    
    assertThat(loaded).as("페치 조인 미적용").isFalse();
  }
  /**
   * 페치조인 적용
   */
  @Disabled
  @Test
  public void fetchJoinUse() {
    em.flush();
    em.clear();
    
    Member result = queryFactory
      .selectFrom(member)
      .join(member.team, team).fetchJoin()
      .where(member.username.eq("member1"))
      .fetchOne();
    
    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(result.getTeam());
    
    assertThat(loaded).as("페치 조인 적용").isTrue();
  }
  
  
  
  
  /************************************************************
   * 서브 쿼리
   ************************************************************/
  /**
   * from 절의 서브쿼리 한계
    JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다.
    당연히 Querydsl도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다.
    Querydsl도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다.
    
   * from 절의 서브쿼리 해결방안
    1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
    2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
    3. nativeSQL을 사용한다
   */
  
  /**
   * 서브 쿼리 eq 사용
   * 나이가 가장 많은 회원 조회
   */
  @Disabled
  @Test
  public void subQuery() {
    QMember memberSub = new QMember("memberSub");
    
    List<Member> result = queryFactory
      .selectFrom(member)
      .where(member.age.eq(
            JPAExpressions
              .select(memberSub.age.max())
              .from(memberSub)
          ))
      .fetch();
    
    assertThat(result).extracting("age").containsExactly(40);
  }
  /**
   * 서브 쿼리 goe 사용
   * 나이가 가장 많은 회원 조회
   */
  @Disabled
  @Test
  public void subQueryGoe() {
    QMember memberSub = new QMember("memberSub");
    
    List<Member> result = queryFactory
      .selectFrom(member)
      .where(member.age.goe(
            select(memberSub.age.avg()) // static import 활용
              .from(memberSub)
          ))
      .fetch();
    
    assertThat(result).extracting("age").containsExactly(30, 40);
  }
  /**
   * 서브쿼리 여러 건 처리, in 사용
   */
  @Disabled
  @Test
  public void subQueryIn() {
    QMember memberSub = new QMember("memberSub");
    
    List<Member> result = queryFactory
      .selectFrom(member)
      .where(member.age.in(
            select(memberSub.age) // static import 활용
              .from(memberSub)
              .where(memberSub.age.gt(10))
          ))
      .fetch();
    
    assertThat(result).extracting("age").containsExactly(20, 30, 40);
  }
  /**
   * select절에 subquery
   */
  @Disabled
  @Test
  public void selectSubQuery() {
    QMember memberSub = new QMember("memberSub");
    
    List<Tuple> result = queryFactory
        .select(member.username
            ,select(memberSub.age.avg()) // static import 활용
              .from(memberSub)
         )
        .from(member)
        .fetch();
    
    for(Tuple tuple : result) {
      System.out.println("username : " + tuple.get(member.username));
      System.out.println("age : " + tuple.get(JPAExpressions.select(memberSub.age.avg()).from(memberSub)));
    }
  }

  /**
   * case문 : 단순 조건
   */
  @Disabled
  @Test
  public void basicCase() {
    
    List<String> result = queryFactory
      .select(member.age.when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
      .from(member)
      .fetch();
    
    for(String s : result) {
      System.out.println(s);
    }
  }
  /**
   * case문 : 복잡한 조건
   */
  @Disabled
  @Test
  public void complexCase() {
    List<String> result = queryFactory
      .select(new CaseBuilder()
              .when(member.age.between(0, 20)).then("0~20살")
              .when(member.age.between(21, 30)).then("21~30살")
              .otherwise("기타"))
      .from(member)
      .fetch();
    
    for(String s : result) {
      System.out.println(s);
    }
  }
  /**
   * case문 : order by에서 case 문 함께 사용
   */
  @Disabled
  @Test
  public void orderByCase() {
    
    NumberExpression<Integer> rankPath = new CaseBuilder()
      .when(member.age.between(0, 20)).then(2)
      .when(member.age.between(21, 30)).then(1)
      .otherwise(3);
    
    List<Tuple> result = queryFactory
      .select(member.username, member.age, rankPath)
      .from(member)
      .orderBy(rankPath.desc())
      .fetch();
    
    for(Tuple tuple : result) {
      System.out.println(String.format("username: %s, age: %s, rank: %s"
                            , tuple.get(member.username)
                            , tuple.get(member.age)
                            , tuple.get(rankPath)
                          )
          );
    }
  }
  
  
  /**
   * 상수
   */
  @Disabled
  @Test
  public void constant() {
    
    List<Tuple> result = queryFactory
      .select(member.username, Expressions.constant("A"))
      .from(member)
      .fetch();
    
    for (Tuple tuple : result) {
      System.out.println(tuple);
    }
  }
  
  /**
   * 문자 더하기
   */
  @Disabled
  @Test
  public void concat() {
    
    List<String> result = queryFactory
      .select(member.username.concat("_").concat(member.age.stringValue()))
      .from(member)
//      .where(member.username.eq("member1"))
      .fetch();
    
    for (String s : result) {
      System.out.println(s);
    }
  }

  
  /**
   * 프로젝션 대상이 하나
   * 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
   * 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회
   */
  @Disabled
  @Test
  public void simpleProjection() { 
    
    List<String> result = queryFactory
      .select(member.username)
      .from(member)
      .fetch();
    
    for (String s : result) {
      System.out.println("username : "+ s);
    }
  }
  @Disabled
  @Test
  public void tupleProjection() {
    
    List<Tuple> result = queryFactory
      .select(member.username, member.age)
      .from(member)
      .fetch();
    
    for (Tuple tuple : result) {
      String username = tuple.get(member.username);
      Integer age = tuple.get(member.age);
      System.out.println(username);
      System.out.println(age);
    }
  }
  
  /**
   * 순수 JPA에서 DTO 조회
   */
  @Disabled
  @Test
  public void findDtoByJPQL() {
    
    List<MemberDto> result = em.createQuery(
          "select new com.kbs.querydsl.dto.MemberDto(m.username, m.age)"
        + " from Member m", MemberDto.class)
      .getResultList();
    
    for (MemberDto memberDto : result) {
      System.out.println(memberDto);
    }
  }
  
  /**
   * querydsl로 DTO 조회
   */
  @Disabled
  @Test
  public void findDtoQueryDsl() {
    
    // 프로퍼티 접근 - setter
    List<MemberDto> result = queryFactory
      .select(Projections.bean(MemberDto.class,
          member.username,
          member.age))
      .from(member)
      .fetch();
    
    for (MemberDto memberDto : result) {
      System.out.println(memberDto);
    }
    
    
    // 필드 직접 접근
    List<MemberDto> result2 = queryFactory
      .select(Projections.fields(MemberDto.class, 
          member.username,
          member.age))
      .from(member)
      .fetch();
    
    for (MemberDto memberDto : result2) {
      System.out.println(memberDto);
    }
    
    
    // 별칭이 다를때
    QMember memberSub = QMember.member;
    List<UserDto> result3 = queryFactory
      .select(Projections.fields(UserDto.class, 
          member.username.as("name"),
          ExpressionUtils.as(
              JPAExpressions
                .select(memberSub.age.max())
                .from(member), "age")
          )
       )
      .from(member)
      .fetch();
    
    for (UserDto dto : result3) {
      System.out.println(dto);
    }
    
    
    // 생성자 사용
    List<MemberDto> result4 = queryFactory
      .select(Projections.constructor(MemberDto.class,
          member.username,
          member.age))
      .from(member)
      .fetch();
    
    for (MemberDto memberDto : result4) {
      System.out.println(memberDto);
    }
  }
  
  
  /**
   * @QueryProjection 활용
   */
  @Disabled
  @Test
  public void findDtoByQueryProjection() {
    
    List<MemberDto> result = queryFactory
      .select(new QMemberDto(member.username, member.age))
      .from(member)
      .fetch();
    
    for (MemberDto memberDto : result) {
      System.out.println(memberDto);
    }
  }
  
  
  /**
   * 동적쿼리 - BooleanBuilder 사용
   */
  @Disabled
  @Test
  public void dynamicQueryBooleanBuilder() {
    
    String usernameParam = "member1";
    Integer ageParam = 10;
    
//    String usernameParam = "member1";
//    Integer ageParam = null;
    
    List<Member> result = searchMember1(usernameParam, ageParam);
    Assertions.assertThat(result.size()).isEqualTo(1);
  }

  private List<Member> searchMember1(String usernameCond, Integer ageCond) {
    
    BooleanBuilder builder = new BooleanBuilder();
    
    if(usernameCond != null) {
      builder.and(member.username.eq(usernameCond));
    }
    
    if(ageCond != null) {
      builder.and(member.age.eq(ageCond));
    }
    
    return queryFactory
              .selectFrom(member)
              .where(builder)
              .fetch();
  }
  
  
  /**
   * 동적쿼리 - Where 다중 파라미터 사용
   */
  @Disabled
  @Test
  public void dynamicQueryWhereParam() {
    
    String usernameParam = "member1";
    Integer ageParam = 10;
    
//    String usernameParam = "member1";
//    Integer ageParam = null;
    
    List<Member> result = searchMember2(usernameParam, ageParam);
    Assertions.assertThat(result.size()).isEqualTo(1);
  }

  private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory
              .selectFrom(member)
//              .where(usernameEq(usernameCond), ageEq(ageCond))
              .where(allEq(usernameCond, ageCond))
              .fetch();
  }

  private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
  }
  private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
  }

  // 조합 가능
  private BooleanExpression allEq(String userNameCond, Integer ageCond) {
    return usernameEq(userNameCond).and(ageEq(ageCond));
  }
  
  
  
  /**
   * 수정, 삭제 벌크 연산
   */
  @Disabled
  @Test
  public void bulkUpdate() {
    
    //쿼리 한번에 대량 데이터 수정
    long count = queryFactory
      .update(member)
      .set(member.username, "비회원")
      .where(member.age.lt(28))
      .execute();
    
    List<Member> result = queryFactory
      .selectFrom(member)
      .fetch();
    
    for (Member m : result) {
      System.out.println(m);
    }
    
    /*
     * 영속성 컨텍스트에 엔티티를 가지고 있는 상태에서 벌크연산을 수행한 경우
     * DB에는 수정이 되지만 영속성 컨텍스트에는 수정이 되기 전의 데이터를 가지고 있으므로
     * flush, clear를 해줘야 함
     */
  }
  @Disabled
  @Test
  public void bulkAdd() {
    
    //기존 숫자에 1 더하기
    long count = queryFactory
      .update(member)
      .set(member.age, member.age.add(1))
      .execute();
  }
  @Disabled
  @Test
  public void bulkDelete() {
    
    //쿼리 한버에 대량 데이터 삭제
    long count = queryFactory
      .delete(member)
      .where(member.age.lt(28))
      .execute();
  }
  
  
  /**
   * SQL Function 호출
   */
  @Test
  public void sqlFunction() {
    List<String> result = queryFactory
      .select(Expressions.stringTemplate("function('replace',{0}, {1}, {2})"
          , member.username , "member", "M"))
      .from(member)
      .fetch();
    
    for (String s : result) {
      System.out.println(s);
    }
    
    
    //lower 같은 ansi 표준 함수들은 querydsl이 상당부분 내장
    List<String> result2 = queryFactory
        .select(member.username)
        .from(member)
//        .where(member.username.eq(Expressions.stringTemplate("function('lower',{0})", member.username)))
        .where(member.username.eq(member.username.lower()))
        .fetch();
      
      for (String s : result2) {
        System.out.println(s);
      }
  }
}
