package com.sookmyung.swapclass.global.config;

import com.sookmyung.swapclass.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (!jwtTokenProvider.validate(token)) {
                    throw new MessagingException("유효하지 않은 토큰입니다.");
                }
                Long userId = jwtTokenProvider.getUserId(token);
                accessor.getSessionAttributes().put("userId", userId);
                log.info("WebSocket 연결 요청 - sessionId: {}, userId: {}", accessor.getSessionId(), userId);
            } else {
                log.info("WebSocket 연결 요청 - sessionId: {}, 토큰 없음", accessor.getSessionId());
            }
        }

        if (StompCommand.DISCONNECT.equals(command)) {
            log.info("WebSocket 연결 해제 - sessionId: {}", accessor.getSessionId());
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {
            log.info("구독 요청 - destination: {}", accessor.getDestination());
        }

        return message;
    }
}