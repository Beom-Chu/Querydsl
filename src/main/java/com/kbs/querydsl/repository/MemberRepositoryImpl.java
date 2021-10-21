package com.kbs.querydsl.repository;

import static com.kbs.querydsl.entity.QMember.member;
import static com.kbs.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.dto.QMemberTeamDto;
import com.kbs.querydsl.entity.Member;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class MemberRepositoryImpl implements MemberRepositoryCustom {
  
  private final JPAQueryFactory queryFactory;
  
  public MemberRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<MemberTeamDto> search(MemberSearchCond cond) {
    return queryFactory
        .select(new QMemberTeamDto(
            member.id, 
            member.username, 
            member.age, 
            team.id, 
            team.name))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(cond.getUsername()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe()))
        .fetch();
  }

  private BooleanExpression usernameEq(String username) {
    return hasText(username) ? member.username.eq(username) : null;
  }

  private BooleanExpression teamNameEq(String teamName) {
    return hasText(teamName) ? member.team.name.eq(teamName) : null;
  }

  private BooleanExpression ageGoe(Integer ageGoe) {
    return ageGoe != null ? member.age.goe(ageGoe) : null;
  }

  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe != null ? member.age.loe(ageLoe) : null;
  }

  @Override
  public Page<MemberTeamDto> searchPageSimple(MemberSearchCond cond, Pageable pageable) {
    
    QueryResults<MemberTeamDto> result = queryFactory
      .select(new QMemberTeamDto(
          member.id, 
          member.username, 
          member.age, 
          team.id, 
          team.name))
      .from(member)
      .leftJoin(member.team, team)
      .where(usernameEq(cond.getUsername()),
              teamNameEq(cond.getTeamName()),
              ageGoe(cond.getAgeGoe()),
              ageLoe(cond.getAgeLoe()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetchResults();
    
    List<MemberTeamDto> content = result.getResults();
    long total = result.getTotal();
    
    return new PageImpl<>(content, pageable, total);
  }

  /**
   * 최적화를 위해 내용 쿼리와 전체 Count 쿼리를 분리하는 경우
   */
  @Override
  public Page<MemberTeamDto> searchPageComplex(MemberSearchCond cond, Pageable pageable) {
    
    List<MemberTeamDto> content = queryFactory
        .select(new QMemberTeamDto(
            member.id, 
            member.username, 
            member.age, 
            team.id, 
            team.name))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(cond.getUsername()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    
    //내용, 카운트 쿼리 분리 기본
//    long total = queryFactory
//        .selectFrom(member)
//        .leftJoin(member.team, team)
//        .where(usernameEq(cond.getUsername()),
//            teamNameEq(cond.getTeamName()),
//            ageGoe(cond.getAgeGoe()),
//            ageLoe(cond.getAgeLoe()))
//        .fetchCount();
//    
//    return new PageImpl<>(content, pageable, total);
    
    
    //countQuery 최적화 - count쿼리가 생략 가능한 경우 생략해서 처리
    JPAQuery<Member> countQuery = queryFactory
        .selectFrom(member)
        .leftJoin(member.team, team)
        .where(usernameEq(cond.getUsername()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe()));
    
    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
  }
  
  
  
}
