package com.plantify.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

//    private Long userId;
    private String sender;
    private String message;
    private MessageType type;
}
