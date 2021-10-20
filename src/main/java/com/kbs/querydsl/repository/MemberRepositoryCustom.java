package com.kbs.querydsl.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.kbs.querydsl.dto.MemberSearchCond;
import com.kbs.querydsl.dto.MemberTeamDto;

public interface MemberRepositoryCustom {
  
  List<MemberTeamDto> search(MemberSearchCond cond);
  
  Page<MemberTeamDto> searchPageSimple(MemberSearchCond cond, Pageable pageable);
  
  Page<MemberTeamDto> searchPageComplex(MemberSearchCond cond, Pageable pageable);
}
