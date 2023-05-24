package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import com.dnlab.tacktogetherbackend.match.domain.MatchInfoMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchInfoMemberRepository extends JpaRepository<MatchInfoMember, Long> {
    MatchInfoMember findMatchInfoMemberByUsername(String username);
}
