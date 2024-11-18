package com.plantify.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String FASTAPI_URL;

    @Override
    public String generateResponse(String userMessage) {
        try {
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("question", userMessage);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload);
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(FASTAPI_URL, requestEntity, Map.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                Map<String, Object> responseBody = responseEntity.getBody();
                return responseBody.get("answer").toString();
            } else {
                return "서버로부터 유효하지 않은 응답";
            }
        } catch (Exception e) {
            return extractDetailFromErrorMessage(e.getMessage());
        }
    }

    private String extractDetailFromErrorMessage(String errorMessage) {
        try {
            int jsonStartIndex = errorMessage.indexOf("{");
            int jsonEndIndex = errorMessage.lastIndexOf("}") + 1;

            if (jsonStartIndex >= 0 && jsonEndIndex > jsonStartIndex) {
                String jsonPart = errorMessage.substring(jsonStartIndex, jsonEndIndex);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(jsonPart);

                if (root.has("detail")) {
                    String detailMessage = root.get("detail").asText();
                    int colonIndex = detailMessage.indexOf(":");
                    if (colonIndex >= 0) {
                        return detailMessage.substring(colonIndex + 1).trim();
                    }
                    return detailMessage;
                }
            }
            return "알 수 없는 에러가 발생했습니다.";
        } catch (Exception e) {
            return "알 수 없는 에러가 발생했습니다.";
        }
    }
}

