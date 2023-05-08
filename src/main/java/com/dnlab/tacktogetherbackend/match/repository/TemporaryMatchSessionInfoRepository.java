package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.redis.TemporaryMatchSessionInfo;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryMatchSessionInfoRepository extends CrudRepository<TemporaryMatchSessionInfo, String> {
    void deleteBySessionId(String sessionId);
}