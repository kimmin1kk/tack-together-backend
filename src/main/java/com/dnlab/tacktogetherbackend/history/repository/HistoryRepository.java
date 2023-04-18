package com.dnlab.tacktogetherbackend.history.repository;

import com.dnlab.tacktogetherbackend.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findHistoriesByMemberUsername(String username);
}
