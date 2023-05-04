package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    List<MatchResult> findTop2ByOrderByCreateTimeDesc();
}
