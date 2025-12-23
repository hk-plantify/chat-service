package com.plantify.chat.service;

import com.plantify.chat.domain.entity.SenderType;
import com.plantify.pb.unit.chat.ChatRequest;
import com.plantify.pb.unit.chat.ChatResponse;
import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatServiceGrpc.ChatServiceStub chatServiceStub;

    @Override
    public Flux<String> streamResponse(String userMessage) {
        ChatRequest request = ChatRequest.newBuilder()
                .setMessage(userMessage)
                .setSender(SenderType.USER.name())
                .build();

        return Flux.create(sink -> {
            StreamObserver<ChatResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(ChatResponse response) {
                    if (!sink.isCancelled()) {
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
                    log.info("gRPC stream completed");
                    sink.complete();
                }
            };

            chatServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .streamMessage(request, responseObserver);

        }, FluxSink.OverflowStrategy.BUFFER);
    }
}