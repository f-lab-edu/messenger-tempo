package com.messenger.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 필요한 권한이 존재하지 않은 경우 403 FORBIDDEN 에러를 리턴하기 위해
// AccessDeniedHandler를 구현한 JwtAccessDeniedHandler 클래스를 작성합니다.
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        //필요한 권한이 없이 접근하려 할때 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("access denied");
        response.getWriter().flush();
    }

}
