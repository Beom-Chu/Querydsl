package com.kbs.querydsl.repository;

import java.util.List;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;

public interface MemberRepositoryCustom {
  
  List<MemberTeamDto> search(MemberSearchCond cond);
}
