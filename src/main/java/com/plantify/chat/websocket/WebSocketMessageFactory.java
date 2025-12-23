package com.plantify.chat.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plantify.chat.domain.entity.ChatMessage;
import com.plantify.chat.domain.entity.SenderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class WebSocketMessageFactory {

    private final ChatMessageMapper mapper;

    public WebSocketMessage chat(WebSocketSession session, SenderType sender, String message) {
        return create(session, ChatMessage.chat(sender, message));
    }

    public WebSocketMessage error(WebSocketSession session, String message) {
        return create(session, ChatMessage.error(message));
    }

    private WebSocketMessage create(WebSocketSession session, ChatMessage message) {
        try {
            return session.textMessage(mapper.toJson(message));

        } catch (JsonProcessingException e) {
            return session.textMessage(
                    "{\"sender\":\"SYSTEM\",\"type\":\"ERROR\",\"message\":\"Serialization error\"}"
            );
        }
    }
}
