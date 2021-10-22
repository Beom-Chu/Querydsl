package com.kbs.querydsl.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.repository.MemberJpaRepository;
import com.kbs.querydsl.repository.MemberRepository;
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
}
