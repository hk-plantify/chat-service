package com.plantify.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantify.chat.domain.entity.ChatMessage;
import com.plantify.chat.domain.entity.MessageType;
import com.plantify.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();

        Flux<WebSocketMessage> messageFlux = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> {
                    try {
                        ChatMessage userMessage = objectMapper.readValue(payload, ChatMessage.class);
                        String aiResponse = chatService.generateResponse(userMessage.getMessage());

                        ChatMessage aiMessage = ChatMessage.builder()
                                .sender("AI")
                                .message(aiResponse)
                                .type(MessageType.CHAT)
                                .build();

                        String response = objectMapper.writeValueAsString(aiMessage);
                        return Mono.just(session.textMessage(response));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("메시지 처리 실패"));
                    }
                });

        return session.send(messageFlux);
    }
}