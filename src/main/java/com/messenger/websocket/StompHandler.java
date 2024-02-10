package com.messenger.websocket;

import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    public StompHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.debug("message = {}, channel = {}", message, channel);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.debug("StompHeaderAccessor = {}", accessor);
        MessageHeaders headers = message.getHeaders();
        log.debug("headers = {}", headers);
        if (accessor != null && (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand()))) {
            String token = accessor.getFirstNativeHeader("Authorization");
            log.debug("token = {}", token);
            if (!tokenProvider.validateToken(token)) {
                throw new MyException(ErrorCode.UNAUTHORIZED);
            }
            log.debug("thread id = {}", Thread.currentThread().getId());
            Authentication authentication = tokenProvider.getAuthentication(token);
            log.debug("auth = {}", authentication);
            accessor.setUser(authentication);
        }
        return message;
    }
}
