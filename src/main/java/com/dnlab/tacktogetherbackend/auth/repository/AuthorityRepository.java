package com.dnlab.tacktogetherbackend.auth.repository;

import com.dnlab.tacktogetherbackend.auth.domain.Authority;
import com.dnlab.tacktogetherbackend.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<List<Authority>> findAuthoritiesByMember(Member member);
    void deleteByMember(Member member);
}
