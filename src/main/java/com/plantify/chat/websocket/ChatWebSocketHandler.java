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
import reactor.core.publisher.SignalType;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> messageFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    try {
                        log.info("payload: {}", payload);
                        ChatMessage userMessage = objectMapper.readValue(payload, ChatMessage.class);
                        return chatService.streamResponse(userMessage.getMessage())
                                .map(reply -> {
                                    try {
                                        ChatMessage aiMessage = ChatMessage.builder()
                                                .sender("AI")
                                                .message(reply)
                                                .type(MessageType.CHAT)
                                                .build();
                                        return session.textMessage(objectMapper.writeValueAsString(aiMessage));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException("JSON 직렬화 실패", e);
                                    }
                                });
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("메시지 처리 실패"));
                    }
                });

        return session.send(messageFlux.onErrorResume(e -> Mono.empty()));
    }
}
