package com.plantify.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatServiceImpl implements ChatService {

    private final RestTemplate restTemplate = new RestTemplate();
//    private final String aiApiUrl = "";

    @Override
    public String generateResponse(String userMessage) {
//        String response = restTemplate.postForObject(aiApiUrl, userMessage, String.class);
//        return response != null ? response : "응답을 생성할 수 없습니다.";
        return userMessage;
    }
}
