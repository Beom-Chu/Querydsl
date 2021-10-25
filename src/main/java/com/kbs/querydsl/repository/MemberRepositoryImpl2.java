package com.kbs.querydsl.repository;

import static com.kbs.querydsl.entity.QMember.member;
import static com.kbs.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.dto.QMemberTeamDto;
import com.kbs.querydsl.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;


/**
 * 리포지토리 지원 - QuerydslRepositorySupport
 * 
 ** 장점
 * getQuerydsl().applyPagination() 스프링 데이터가 제공하는 페이징을 Querydsl로 편리하게 변환 가능(단! Sort는 오류발생)
 * from() 으로 시작 가능(최근에는 QueryFactory를 사용해서 select() 로 시작하는 것이 더 명시적)
 * EntityManager 제공
 * 
 ** 한계
 * Querydsl 3.x 버전을 대상으로 만듬
 * Querydsl 4.x에 나온 JPAQueryFactory로 시작할 수 없음
 * select로 시작할 수 없음 (from으로 시작해야함)
 * QueryFactory 를 제공하지 않음
 * 스프링 데이터 Sort 기능이 정상 동작하지 않음
 */
public class MemberRepositoryImpl2 extends QuerydslRepositorySupport implements MemberRepositoryCustom {

  public MemberRepositoryImpl2() {
    super(Member.class);
  }

  @Override
  public List<MemberTeamDto> search(MemberSearchCond cond) {
    return from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(cond.getUsername()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe()))
        .select(new QMemberTeamDto(
            member.id, 
            member.username, 
            member.age, 
            team.id, 
            team.name))
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
    
    JPQLQuery<MemberTeamDto> jpaQuery = from(member)
      .leftJoin(member.team, team)
      .where(usernameEq(cond.getUsername()),
              teamNameEq(cond.getTeamName()),
              ageGoe(cond.getAgeGoe()),
              ageLoe(cond.getAgeLoe()))
      .select(new QMemberTeamDto(
          member.id, 
          member.username, 
          member.age, 
          team.id, 
          team.name));
    
    JPQLQuery<MemberTeamDto> result = getQuerydsl().applyPagination(pageable, jpaQuery);
    
    List<MemberTeamDto> content = result.fetch();
    long total = result.fetchCount();
    
    return new PageImpl<>(content, pageable, total);
  }

  /**
   * 최적화를 위해 내용 쿼리와 전체 Count 쿼리를 분리하는 경우
   */
  @Override
  public Page<MemberTeamDto> searchPageComplex(MemberSearchCond cond, Pageable pageable) {
    
    List<MemberTeamDto> content = from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(cond.getUsername()),
                teamNameEq(cond.getTeamName()),
                ageGoe(cond.getAgeGoe()),
                ageLoe(cond.getAgeLoe()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .select(new QMemberTeamDto(
            member.id, 
            member.username, 
            member.age, 
            team.id, 
            team.name))
        .fetch();
    
    //내용, 카운트 쿼리 분리 기본
    long total = from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(cond.getUsername()),
            teamNameEq(cond.getTeamName()),
            ageGoe(cond.getAgeGoe()),
            ageLoe(cond.getAgeLoe()))
        .select(member)
        .fetchCount();
    
    return new PageImpl<>(content, pageable, total);
    
  }
  
  
  
}
