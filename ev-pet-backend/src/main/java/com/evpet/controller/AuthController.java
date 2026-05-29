package com.evpet.controller;

import com.evpet.dto.LoginDTO;
import com.evpet.service.AuthService;
import com.evpet.utils.JwtUtil;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.LoginVO;
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
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        try {
            LoginVO result = authService.login(dto);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
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
