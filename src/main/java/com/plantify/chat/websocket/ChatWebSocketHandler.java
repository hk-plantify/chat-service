package com.plantify.chat.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantify.chat.domain.entity.ChatMessage;
import com.plantify.chat.domain.entity.MessageType;
import com.plantify.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> incomingMessages = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> handleMessage(payload, session))
                .onErrorResume(e -> handleWebSocketError(session, e));

        return session.send(incomingMessages);
    }

    private Flux<WebSocketMessage> handleMessage(String payload, WebSocketSession session) {
        try {
            log.info("Received payload: {}", payload);
            ChatMessage userMessage = objectMapper.readValue(payload, ChatMessage.class);

            return chatService.streamResponse(userMessage.getMessage())
                    .map(reply -> createWebSocketMessage("AI", reply, MessageType.CHAT, session))
                    .onErrorResume(e -> Flux.just(createWebSocketMessage("System", "Error in AI service", MessageType.ERROR, session)));

        } catch (JsonProcessingException e) {
            log.error("Invalid message format", e);
            return Flux.just(createWebSocketMessage("System", "Invalid message format", MessageType.ERROR, session));
        }
    }

    private Flux<WebSocketMessage> handleWebSocketError(WebSocketSession session, Throwable e) {
        log.error("WebSocket error: ", e);
        return Flux.just(createWebSocketMessage("System", "An error occurred: " + e.getMessage(), MessageType.ERROR, session));
    }

    private WebSocketMessage createWebSocketMessage(String sender, String message, MessageType type, WebSocketSession session) {
        try {
            ChatMessage chatMessage = ChatMessage.builder()
                    .sender(sender)
                    .message(message)
                    .type(type)
                    .build();
            return session.textMessage(objectMapper.writeValueAsString(chatMessage));
        } catch (JsonProcessingException e) {
            log.error("Error serializing message", e);
            return session.textMessage("{\"sender\":\"System\",\"message\":\"Critical error\",\"type\":\"ERROR\"}");
        }
    }
}
