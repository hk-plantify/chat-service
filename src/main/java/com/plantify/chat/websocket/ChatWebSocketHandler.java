package com.plantify.chat.websocket;

import com.plantify.chat.domain.entity.SenderType;
import com.plantify.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final ChatMessageMapper messageMapper;
    private final WebSocketMessageFactory messageFactory;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> outgoing =
                session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .flatMap(payload -> handleMessage(payload, session))
                        .onErrorResume(e ->
                                Flux.just(
                                        messageFactory.error(
                                                session,
                                                "WebSocket error occurred"
                                        )
                                )
                        );

        return session.send(outgoing);
    }

    private Flux<WebSocketMessage> handleMessage(String payload, WebSocketSession session) {
        return Mono.fromCallable(() -> messageMapper.fromJson(payload))
                .flatMapMany(userMessage ->
                        chatService.streamResponse(userMessage.getMessage())
                                .map(reply ->
                                        messageFactory.chat(
                                                session,
                                                SenderType.AI,
                                                reply
                                        )
                                )
                )
                .onErrorResume(e -> {
                    log.error("Message handling error", e);
                    return Flux.just(
                            messageFactory.error(
                                    session,
                                    "Invalid message or AI service error"
                            )
                    );
                });
    }
}
