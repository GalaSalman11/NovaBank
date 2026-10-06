package com.novabank.novabank_registration.security;

import com.novabank.novabank_registration.entity.Platform;
import com.novabank.novabank_registration.entity.UserSession;
import com.novabank.novabank_registration.repository.UserSessionRepository;   // adjust package
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;
    private final UserSessionRepository sessionRepository;
    private final List<String> publicPaths;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.info("JWT filter: {} {} auth={}", request.getMethod(), request.getServletPath(),
                request.getHeader("Authorization") != null ? "present" : "missing");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtUtil.parse(authHeader.substring(7));


            String header = request.getHeader("X-Channel");
            if (header == null || header.isBlank()) {
                writeError(response, 400, "CHANNEL_REQUIRED", "X-Channel header is required");
                return;
            }
            Platform requested;
            try {
                requested = Platform.valueOf(header.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                writeError(response, 400, "CHANNEL_INVALID", "Unknown platform");
                return;
            }


            UserSession session = sessionRepository
                    .findById(UUID.fromString(claims.getId())).orElse(null);
            if (session == null || !session.isActive()
                    || session.getExpiresAt().isBefore(Instant.now())) {
                writeError(response, 401, "SESSION_INVALIDATED",
                        "You were signed out because of a login elsewhere");
                return;
            }


            if (session.getPlatform() != requested) {
                log.warn("Platform mismatch for customer {}", maskEmail(claims.getSubject()));
                writeError(response, 401, "SESSION_PLATFORM_MISMATCH",
                        "Session is not valid on this platform");
                return;
            }

            List<String> roles = claims.get("roles", List.class);
            var authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null,
                    AuthorityUtils.createAuthorityList(roles.toArray(new String[0])));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            writeError(response, 401, "TOKEN_EXPIRED", "Token has expired");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            writeError(response, 401, "INVALID_TOKEN", "Invalid token");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return publicPaths.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    private void writeError(HttpServletResponse response, int status,
                            String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"errorCode\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        int at = email.indexOf('@');
        return email.charAt(0) + "***" + email.substring(at);
    }
}