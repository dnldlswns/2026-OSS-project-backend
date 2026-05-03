package oss.backend.global.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oss.backend.global.response.JwtTokenProvider;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                String token = extractToken(request);
                if (token != null && jwtTokenProvider.validateToken(token)) {
                        String userId = jwtTokenProvider.getUserId(token);
                        UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(userId, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                }
                filterChain.doFilter(request, response);
        }

        private String extractToken(HttpServletRequest request) {
                String header = request.getHeader("Authorization");
                if (header != null && header.startsWith("Bearer ")) {
                        return header.substring(7);
                }
                return null;
        }
}
