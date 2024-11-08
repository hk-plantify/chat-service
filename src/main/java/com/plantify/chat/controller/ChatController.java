package com.plantify.chat.controller;

import com.plantify.chat.domain.entity.ChatMessage;
import com.plantify.chat.domain.entity.MessageType;
import com.plantify.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {

        ChatMessage response = ChatMessage.builder()
                .sender("AI")
                .message(chatService.generateResponse(chatMessage.getMessage()))
                .type(MessageType.CHAT)
                .build();

        messagingTemplate.convertAndSend("/topic/public", response);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setType(MessageType.JOIN);
        return chatMessage;
    }
}
