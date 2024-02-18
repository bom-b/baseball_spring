package com.baseball.config;

import com.baseball.utils.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/game")
            .setAllowedOrigins("http://localhost:3000")
            .addInterceptors(new HandshakeInterceptor() {
                @Override
                public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

                    // 요청 URL에서 쿼리 파라미터 문자열 추출
                    String query = request.getURI().getQuery();

                    // 쿼리 파라미터 문자열을 분석하여 토큰을 추출
                    String token = Arrays.stream(query.split("&"))
                        .map(s -> s.split("="))
                        .filter(strings -> strings[0].equals("token"))
                        .findFirst()
                        .map(strings -> strings[1])
                        .orElse(null);

                    Integer id = JwtUtil.getID(token);

                    if(id != null) {
                        // 사용자 id를 websocket 세션에 저장
                        attributes.put("userID", id);
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

                }
            })
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//                // 여기에서 메시지 유형에 따른 인증 및 인가 로직을 추가할 수 있습니다.
//
//                return message;
//            }
//        });
//    }
}
