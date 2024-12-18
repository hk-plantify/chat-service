package com.plantify.chat.config;

import com.plantify.chat.client.AuthServiceClient;
import com.plantify.chat.domain.dto.AuthUserResponse;
import com.plantify.chat.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthServiceClient authServiceClient;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String accessToken = extractTokenFromQuery(request);

        if (accessToken != null) {
            try {
                ApiResponse<AuthUserResponse> authResponse = authServiceClient.getUserInfo("Bearer " + accessToken);
                if (authResponse.getStatus() == HttpStatus.OK.value() && authResponse.getData() != null) {
                    AuthUserResponse userInfo = authResponse.getData();
                    attributes.put("userInfo", userInfo);
                    log.info("Authenticated userId: {}", userInfo.userId());
                    return true;
                }
            } catch (Exception e) {
                log.error("Token validation failed: {}", e.getMessage());
            }
        }

        log.warn("WebSocket handshake failed: Invalid or missing token");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false; // Handshake 실패
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractTokenFromQuery(ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        List<String> tokens = queryParams.get("accessToken");
        if (tokens != null && !tokens.isEmpty()) {
            return tokens.get(0);
        }
        return null;
    }
}
