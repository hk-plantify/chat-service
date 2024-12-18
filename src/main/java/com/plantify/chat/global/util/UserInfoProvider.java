package com.plantify.chat.global.util;

import com.plantify.chat.domain.dto.AuthUserResponse;
import com.plantify.chat.global.exception.ApplicationException;
import com.plantify.chat.global.exception.errorCode.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserInfoProvider {

    public AuthUserResponse getUserInfoFromAttributes(Map<String, Object> attributes) {
        AuthUserResponse userInfo = (AuthUserResponse) attributes.get("userInfo");
        if (userInfo == null) {
            throw new ApplicationException(AuthErrorCode.INVALID_TOKEN);
        }
        return userInfo;
    }
}
