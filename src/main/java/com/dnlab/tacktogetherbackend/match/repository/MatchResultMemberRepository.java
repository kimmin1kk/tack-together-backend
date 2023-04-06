package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchResultMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchResultMemberRepository extends JpaRepository<MatchResultMember, Long> {
}
