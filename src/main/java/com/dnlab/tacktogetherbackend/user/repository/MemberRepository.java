package com.dnlab.tacktogetherbackend.user.repository;

import com.dnlab.tacktogetherbackend.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByUsername(String username);
    boolean existsByUsername(String username);
}
