package com.dnlab.tacktogetherbackend.user.repository;

import com.dnlab.tacktogetherbackend.user.domain.Authority;
import com.dnlab.tacktogetherbackend.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Set<Authority>> findAuthoritiesByMember(Member member);
}
