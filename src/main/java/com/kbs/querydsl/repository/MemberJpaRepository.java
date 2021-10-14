package com.kbs.querydsl.repository;

import static com.kbs.querydsl.entity.QMember.member;
import static com.kbs.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.dto.QMemberTeamDto;
import com.kbs.querydsl.entity.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;


/**
 * 순수 JPA 리포지토리와 Querydsl
 */
@Repository
public class MemberJpaRepository {

  private final EntityManager em;
  private final JPAQueryFactory queryFactory;

  public MemberJpaRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }


  public void save(Member member) {
    em.persist(member);
  }

  public Optional<Member> findById(Long id) {
    Member findMember = em.find(Member.class, id);
    return Optional.ofNullable(findMember);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class)
        .getResultList();
  }
  
  public List<Member> findByUsername(String username) {
    return em.createQuery("select m from Member m where m.username = :username", Member.class)
          .setParameter("username", username)
          .getResultList();
  }
  
  
  public List<Member> findAll_Querydsl(){
    return queryFactory
         .selectFrom(member).fetch();
  }
  
  public List<Member> findByusername_Querydsl(String username) {
    return queryFactory
        .selectFrom(member)
        .where(member.username.eq(username))
        .fetch();
  }
  
  /**
   * 동적쿼리 - Builder 사용
   */
  public List<MemberTeamDto> searchByBuilder(MemberSearchCond cond) {
    
    BooleanBuilder builder = new BooleanBuilder();
    if(hasText(cond.getUsername())) {
      builder.and(member.username.eq(cond.getUsername()));
    }
    if(hasText(cond.getTeamName())) {
      builder.and(team.name.eq(cond.getTeamName()));
    }
    if(cond.getAgeGoe() != null) {
      builder.and(member.age.goe(cond.getAgeGoe()));
    }
    if(cond.getAgeLoe() != null) {
      builder.and(member.age.loe(cond.getAgeLoe()));
    }
    
    return queryFactory
        .select(new QMemberTeamDto(
            member.id, 
            member.username, 
            member.age, 
            team.id, 
            team.name))
        .from(member)
        .leftJoin(member.team, team)
        .where(builder)
        .fetch();
  }
  
  /**
   * 동적쿼리 Where절 파라미터 사용
   */
  public List<MemberTeamDto> searchByWhere(MemberSearchCond cond) {
    
    return queryFactory
         .select(new QMemberTeamDto(
             member.id,
             member.username,
             member.age,
             team.id,
             team.name
             ))
         .from(member)
         .leftJoin(member.team, team)
         .where(
             usernameEq(cond.getUsername()),
             teamNameEq(cond.getTeamName()),
             ageGoe(cond.getAgeGoe()),
             ageLoe(cond.getAgeLoe())
             )
         .fetch();
  }


  private BooleanExpression usernameEq(String username) {
    return hasText(username) ? member.username.eq(username) : null;
  }

  private BooleanExpression teamNameEq(String teamName) {
    return hasText(teamName) ? team.name.eq(teamName) : null;
  }

  private BooleanExpression ageGoe(Integer ageGoe) {
    return ageGoe != null ? member.age.goe(ageGoe) : null;
  }

  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe != null ? member.age.loe(ageLoe) : null;
  }
}
