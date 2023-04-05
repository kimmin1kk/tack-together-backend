package com.dnlab.tacktogetherbackend.match.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "match.range")
@Data
public class MatchRangeProperties {
    private Range origin;
    private Range destination;

    @Data
    public static class Range {
        private int narrow;
        private int normal;
        private int wide;
    }
}
