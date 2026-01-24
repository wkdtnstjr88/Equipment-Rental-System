package com.example.shiftmate;


import com.example.shiftmate.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // CORS preflight 要請は通過する(통과)
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authenticationが要らない場合は通過する
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization HeaderからTokenを出す
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"認証が必要です。\"}");
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 除去

        // Token　有効性検証
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"無効なトークンです。\"}");
            return;
        }

        // Tokenからユーザー情報を出してRequestに貯蔵　
        Long userNumber = jwtUtil.getUserNumberFromToken(token);
        String userId = jwtUtil.getUserIdFromToken(token);
        String userType = jwtUtil.getUserTypeFromToken(token);

        request.setAttribute("userNumber", userNumber);
        request.setAttribute("userId", userId);
        request.setAttribute("userType", userType);

        // 次のFilterで進め
        filterChain.doFilter(request, response);
    }

    /**
     * Authenticationが要らない公開経路確認　
     */
    private boolean isPublicPath(String path) {
        return path.equals("/api/users/register") ||
                path.equals("/api/users/login") ||
                path.equals("/api/users/check-duplicate") ||
                path.startsWith("/error");
    }
}
