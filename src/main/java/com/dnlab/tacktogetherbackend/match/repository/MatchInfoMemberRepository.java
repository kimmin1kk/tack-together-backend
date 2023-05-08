package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchInfoMemberRepository extends JpaRepository<MatchInfoMember, Long> {
    List<MatchInfoMember> findMatchInfoMembersByMemberUsername(String username);
    List<MatchInfoMember> findMatchInfoMembersByMatchInfo(MatchInfo matchInfo);
}
