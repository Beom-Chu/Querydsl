package com.kbs.querydsl.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kbs.querydsl.dto.MemberDto;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.entity.Member;
import com.kbs.querydsl.repository.MemberJpaRepository;
import com.kbs.querydsl.repository.MemberRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberJpaRepository memberJpaRepository;
  private final MemberRepository memberRepository;
  
  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCond cond) {
    return memberJpaRepository.searchByWhere(cond);
  }
  
  @GetMapping("/v2/members")
  public Page<MemberTeamDto> searchMemberV2(MemberSearchCond cond, Pageable pageable) {
    return memberRepository.searchPageSimple(cond, pageable);
  }
  
  @GetMapping("/v3/members")
  public Page<MemberTeamDto> searchMemberV3(MemberSearchCond cond, Pageable pageable) {
    return memberRepository.searchPageComplex(cond, pageable);
  }
  
  /**
   * Querydsl Web 지원
   * @param predicate
   * @param pageable
   * @return
   * 단순한 조건만 가능
   * 조건을 커스텀하는 기능이 복잡하고 명시적이지 않음
   * 컨트롤러가 Querydsl에 의존
   * 복잡한 실무환경에서 사용하기에는 한계가 명확
   */
  @GetMapping("/v4/members")
  public Page<MemberDto> searchMemberV4(@QuerydslPredicate(root = Member.class) Predicate predicate, Pageable pageable) {

    Page<Member> members = memberRepository.findAll(predicate, pageable);

    return members.map(o -> new MemberDto(o.getUsername(), o.getAge()));
  }
}
