package com.plantify.chat.service;

import com.plantify.pb.unit.chat.ChatRequest;
import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.plantify.pb.unit.chat.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatServiceGrpc.ChatServiceStub chatServiceStub;

    @Override
    public Flux<String> streamResponse(String userMessage) {
        ChatRequest request = ChatRequest.newBuilder()
                .setMessage(userMessage)
                .setSender("User")
                .build();

        log.info("gRPC request: {}", userMessage);

        return Flux.create(sink -> {
            StreamObserver<ChatResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(ChatResponse response) {
                    if (!sink.isCancelled()) {
                        log.info("gRPC response: {}", response);
                        sink.next(response.getReply());
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!sink.isCancelled()) {
                        log.error("gRPC on error: {}", t.getMessage());
                        sink.error(t);
                    }
                }

                @Override
                public void onCompleted() {
                    if (!sink.isCancelled()) {
                        log.info("gRPC onCompleted");
                        sink.complete();
                    }
                }
            };

            try {
                chatServiceStub.streamMessage(request, responseObserver);
                log.info("gRPC request sent");
            } catch (Exception e) {
                log.error("Error sending gRPC request", e);
                sink.error(e);
            }
        }, FluxSink.OverflowStrategy.BUFFER);
    }
}