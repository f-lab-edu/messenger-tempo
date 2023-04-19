package com.messenger.util;

import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {

    private SpringSecurityUtil() {}

    public static String getAuthenticationName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new MyException(ErrorCode.UNAUTHORIZED);
        }
        return authentication.getName();
    }
}
