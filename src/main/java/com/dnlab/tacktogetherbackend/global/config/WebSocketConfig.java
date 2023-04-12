package com.dnlab.tacktogetherbackend.global.config;

import com.dnlab.tacktogetherbackend.auth.common.JwtHandshakeInterceptor;
import com.dnlab.tacktogetherbackend.auth.common.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/match", "/matched")
                .setAllowedOrigins("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenProvider))
                .withSockJS()
                .setHeartbeatTime(10_100);

    }
}
