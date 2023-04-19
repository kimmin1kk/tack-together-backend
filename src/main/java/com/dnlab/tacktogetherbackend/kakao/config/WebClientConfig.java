package com.dnlab.tacktogetherbackend.kakao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final String mobilityBaseUrl;
    private final String kakaoApiKey;

    public WebClientConfig(@Value("${api.kakao.mobility-base-url}") String mobilityBaseUrl,
                           @Value("${api.kakao.key}") String kakaoApiKey) {
        this.mobilityBaseUrl = mobilityBaseUrl;
        this.kakaoApiKey = kakaoApiKey;
    }

    @Bean
    public ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        return factory;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(mobilityBaseUrl)
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
