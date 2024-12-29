package com.codeWithSrb.BookYourSlot.filter;

import com.codeWithSrb.BookYourSlot.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.codeWithSrb.BookYourSlot.util.ExceptionUtils.processError;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
public class CustomizeAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String EMAIL_KEY = "email";
    private static final String TOKEN_KEY = "token";
    private static final String[] PUBLIC_ROUTES = {"/api/v1/booking/register",
            "/api/v1/booking/login",
            "/api/v1/booking/password-reset",
            "/api/v1/booking/verify/password/**",
            "/api/v1/booking/password-reset/key"};

    private static final String MISSING_TOKE_EXCEPTION = "Invalid or missing authorization token";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final TokenProvider tokenProvider;

    public CustomizeAuthorizationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String, String> values = getRequestValues(request);
            String token = values.get(TOKEN_KEY);
            String email = values.get(EMAIL_KEY);

            if(isValidRequest(values, request)) {
                List<GrantedAuthority> grantedAuthorities = tokenProvider.getGrantedAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(email, grantedAuthorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch(Exception e) {
            processError(request, response, e);
        }
    }

    private boolean isValidRequest(Map<String, String> values, HttpServletRequest request) {
        return tokenProvider.isSubjectAndTokenValid(values.get(EMAIL_KEY), values.get(TOKEN_KEY), request);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(PUBLIC_ROUTES)
                .anyMatch(route -> pathMatcher.match(route, request.getRequestURI()));
    }

    private Map<String, String> getRequestValues(HttpServletRequest request) {
        String token = extractToken(request).orElseThrow(() -> new IllegalArgumentException(MISSING_TOKE_EXCEPTION));
        return Map.of(EMAIL_KEY, tokenProvider.getSubject(token, request), TOKEN_KEY, token);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(header -> header.substring(TOKEN_PREFIX.length()));
    }
}