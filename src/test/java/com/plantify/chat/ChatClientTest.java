package com.plantify.chat;

import com.plantify.pb.unit.chat.ChatRequest;
import com.plantify.pb.unit.chat.ChatResponse;
import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChatClientTest {

    @Value("${grpc.server.address}")
    private String grpcServerAddress;

    @Test
    public void testGrpcCommunication() {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcServerAddress)
                .usePlaintext()
                .build();

        ChatServiceGrpc.ChatServiceBlockingStub stub = ChatServiceGrpc.newBlockingStub(channel);

        try {

            ChatRequest request = ChatRequest.newBuilder()
                    .setMessage("Hello from gRPC Test")
                    .setSender("TestClient")
                    .build();

            ChatResponse response = stub.sendMessage(request);
            System.out.println("gRPC Response: " + response.getReply());
            System.out.println("gRPC Status: " + response.getStatus().getMessage());

            assertEquals(200, response.getStatus().getCode());
            assertEquals("Success", response.getStatus().getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("gRPC 통신 실패: " + e.getMessage());
        } finally {
            channel.shutdown();
        }
    }
}