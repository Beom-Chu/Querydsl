package com.kbs.querydsl.repository;

import static com.kbs.querydsl.entity.QMember.member;
import static com.kbs.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;
import java.util.List;
import javax.persistence.EntityManager;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.dto.QMemberTeamDto;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
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
  
  
}
