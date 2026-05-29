package com.evpet.config;

import com.evpet.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow OPTIONS requests for CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            sendError(response, 401, "未登录");
            return false;
        }

        String actualToken = token.trim();
        if (actualToken.toLowerCase().startsWith("bearer ")) {
            actualToken = actualToken.substring(7);
        }

        if (!jwtUtil.validateToken(actualToken)) {
            sendError(response, 401, "Token无效或已过期");
            return false;
        }

        return true;
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}
