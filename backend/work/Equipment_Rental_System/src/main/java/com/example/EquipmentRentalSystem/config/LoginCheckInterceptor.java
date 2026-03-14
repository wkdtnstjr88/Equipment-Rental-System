package com.example.EquipmentRentalSystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            // 로그인되지 않은 사용자가 대여 등을 시도할 경우 로그인 페이지로 리다이렉트
            // 로그인 성공 후 원래 보려던 페이지로 보내주기 위해 redirectURL 파라미터를 추가합니다.
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false; // 컨트롤러 진입 차단
        }

        return true; // 로그인 되어 있으면 정상 진행
    }
}