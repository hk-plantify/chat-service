package com.plantify.chat.service;

import com.plantify.pb.unit.chat.ChatRequest;
import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.plantify.pb.unit.chat.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

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
                        sink.error(t);
                    }
                }

                @Override
                public void onCompleted() {
                    if (!sink.isCancelled()) {
                        sink.complete();
                    }
                }
            };

            try {
                chatServiceStub.streamMessage(request, responseObserver);
            } catch (Exception e) {
                sink.error(e);
            }
        }, FluxSink.OverflowStrategy.BUFFER);
    }

}

