package com.sookmyung.swapclass.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            // TODO: JWT 구현 후 토큰 검증 로직 추가
            // String token = accessor.getFirstNativeHeader("Authorization");
            log.info("WebSocket 연결 요청 - sessionId: {}", accessor.getSessionId());
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
