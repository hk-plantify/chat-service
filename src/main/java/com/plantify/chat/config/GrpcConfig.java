package com.plantify.chat.config;

import com.plantify.pb.unit.chat.ChatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Value("${grpc.server.address}")
    private String grpcServerAddress;

    @Bean
    public ChatServiceGrpc.ChatServiceBlockingStub chatServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcServerAddress)
                .usePlaintext()
                .build();
        return ChatServiceGrpc.newBlockingStub(channel);
    }
}
