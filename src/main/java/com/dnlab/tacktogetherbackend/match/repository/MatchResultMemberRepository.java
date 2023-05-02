package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchResult;
import com.dnlab.tacktogetherbackend.match.domain.MatchResultMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultMemberRepository extends JpaRepository<MatchResultMember, Long> {
    List<MatchResultMember> findMatchResultMembersByMemberUsername(String username);
    List<MatchResultMember> findMatchResultMembersByMatchResult(MatchResult matchResult);
}
