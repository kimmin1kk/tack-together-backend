package com.dnlab.tacktogetherbackend.matched.repository;

import com.dnlab.tacktogetherbackend.matched.domain.redis.MatchSessionInfo;
import org.springframework.data.repository.CrudRepository;

public interface MatchSessionInfoRepository extends CrudRepository<MatchSessionInfo, String> {
}
