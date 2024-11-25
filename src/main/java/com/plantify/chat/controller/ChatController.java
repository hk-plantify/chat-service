package com.plantify.chat.controller;

import com.plantify.chat.domain.entity.ChatMessage;
import com.plantify.chat.domain.entity.MessageType;
import com.plantify.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        chatMessage.setType(MessageType.CHAT);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);

                ChatMessage aiResponse = ChatMessage.builder()
                        .sender("AI")
                        .message(chatService.generateResponse(chatMessage.getMessage()))
                        .type(MessageType.CHAT)
                        .build();

                messagingTemplate.convertAndSend("/topic/public", aiResponse);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setType(MessageType.JOIN);
        return chatMessage;
    }
}
