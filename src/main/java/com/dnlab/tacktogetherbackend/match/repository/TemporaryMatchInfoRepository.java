package com.dnlab.tacktogetherbackend.match.repository;

import com.dnlab.tacktogetherbackend.match.domain.TemporaryMatchInfo;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryMatchInfoRepository extends CrudRepository<TemporaryMatchInfo, String> {
}