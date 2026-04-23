package com.evpet.controller;

import com.evpet.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public abstract class BaseController {

    @Autowired
    protected JwtUtil jwtUtil;

    protected Long getUserIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token不能为空");
        }
        String actualToken = token.trim();
        if (actualToken.toLowerCase().startsWith("bearer ")) {
            actualToken = actualToken.substring(7);
        }
        if (actualToken.isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }
        if (!jwtUtil.validateToken(actualToken)) {
            throw new IllegalArgumentException("Token无效或已过期");
        }
        return jwtUtil.getUserIdFromToken(actualToken);
    }
}
