package com.dnlab.tacktogetherbackend.global.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.redis")
@Data
public class RedisEntityProperties {
    private int ttl;
}