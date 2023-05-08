package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.MatchInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchInfoRepository extends JpaRepository<MatchInfo, Long> {
    List<MatchInfo> findTop2ByOrderByCreateTimeDesc();
}
