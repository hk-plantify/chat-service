package com.plantify.chat.service;

import reactor.core.publisher.Flux;

public interface ChatService {

    Flux<String> streamResponse(String userMessage);
}
