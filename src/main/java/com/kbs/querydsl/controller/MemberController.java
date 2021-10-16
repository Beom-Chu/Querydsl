package com.kbs.querydsl.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;
import com.kbs.querydsl.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberJpaRepository memberJpaRepository;
  
  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCond cond) {
    return memberJpaRepository.searchByWhere(cond);
  }
}
