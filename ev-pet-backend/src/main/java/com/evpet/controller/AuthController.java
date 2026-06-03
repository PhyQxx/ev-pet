package com.evpet.controller;

import com.evpet.dto.LoginDTO;
import com.evpet.service.AuthService;
import com.evpet.utils.JwtUtil;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.LoginVO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);
            LoginVO result = authService.login(dto, clientIp);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @GetMapping("/verify")
    public ApiResponse<Long> verify(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            if (jwtUtil.validateToken(actualToken)) {
                Long userId = jwtUtil.getUserIdFromToken(actualToken);
                return ApiResponse.success(userId);
            }
            return ApiResponse.error(401, "Token无效");
        } catch (Exception e) {
            return ApiResponse.error(401, "Token验证失败");
        }
    }

    @DeleteMapping("/account")
    public ApiResponse<String> deleteAccount(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(actualToken)) {
                return ApiResponse.error(401, "Token无效");
            }
            Long userId = jwtUtil.getUserIdFromToken(actualToken);
            authService.deleteAccount(userId);
            return ApiResponse.success("账号已注销");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("注销失败");
        }
    }
}
