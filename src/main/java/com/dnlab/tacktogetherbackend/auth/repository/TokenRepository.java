package com.dnlab.tacktogetherbackend.auth.repository;

import com.dnlab.tacktogetherbackend.auth.domain.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface TokenRepository extends CrudRepository<RefreshToken, String> {
    Set<RefreshToken> findRefreshTokensByUsername(String username);
}
