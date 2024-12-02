package com.plantify.chat.service;

import com.plantify.pb.unit.chat.ChatRequest;
import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.plantify.pb.unit.chat.ChatResponse;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatServiceGrpc.ChatServiceBlockingStub chatServiceStub;

    @Override
    public String generateResponse(String userMessage) {
        try {
            ChatRequest request = ChatRequest.newBuilder()
                    .setMessage(userMessage)
                    .setSender("User")
                    .build();

            ChatResponse response = chatServiceStub.sendMessage(request);
            return response.getReply();
        } catch (StatusRuntimeException e) {
            return "AI 서버와의 통신 중 오류 발생: " + e.getStatus().getDescription();
        } catch (Exception e) {
            return "알 수 없는 오류 발생: " + e.getMessage();
        }
    }
}

