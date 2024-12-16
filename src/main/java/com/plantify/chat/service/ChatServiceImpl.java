package com.plantify.chat.service;

import com.plantify.pb.unit.chat.ChatRequest;
import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.plantify.pb.unit.chat.ChatResponse;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatServiceGrpc.ChatServiceStub chatServiceStub;

//    @Override
//    public String generateResponse(String userMessage) {
//        try {
//            ChatRequest request = ChatRequest.newBuilder()
//                    .setMessage(userMessage)
//                    .setSender("User")
//                    .build();
//
//            ChatResponse response = chatServiceStub.sendMessage(request);
//            return response.getReply();
//        } catch (StatusRuntimeException e) {
//            return "AI 서버와의 통신 중 오류 발생: " + e.getStatus().getDescription();
//        } catch (Exception e) {
//            return "알 수 없는 오류 발생: " + e.getMessage();
//        }
//    }

    @Override
    public Flux<String> streamResponse(String userMessage) {
        ChatRequest request = ChatRequest.newBuilder()
                .setMessage(userMessage)
                .setSender("User")
                .build();

        return Flux.create(sink -> {
            StreamObserver<ChatResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(ChatResponse response) {
                    sink.next(response.getReply());
                }

                @Override
                public void onError(Throwable t) {
                    sink.error(t);
                }

                @Override
                public void onCompleted() {
                    sink.complete();
                }
            };

            chatServiceStub.streamMessage(request, responseObserver);
        });
    }
}

