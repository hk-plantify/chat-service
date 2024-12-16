package com.plantify.chat.service;

import reactor.core.publisher.Flux;

public interface ChatService {

//    String generateResponse(String userMessage);
    Flux<String> streamResponse(String userMessage);
}
