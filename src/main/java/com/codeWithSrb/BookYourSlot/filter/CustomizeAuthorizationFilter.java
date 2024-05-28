package com.codeWithSrb.BookYourSlot.filter;

import com.codeWithSrb.BookYourSlot.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.codeWithSrb.BookYourSlot.util.ExceptionUtils.processError;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
public class CustomizeAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String EMAIL_KEY = "email";
    private static final String TOKEN_KEY = "token";
    private static final String[] PUBLIC_ROUTES = {"/api/v1/booking/register", "/api/v1/booking/login", "/api/v1/booking/refresh/token"};
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String, String> values = getRequestValues(request);
            String token = getToken(request);
            if(tokenProvider.isTokenValid(values.get(EMAIL_KEY), token)) {
                List<GrantedAuthority> grantedAuthorities = tokenProvider.getGrantedAuthorities(values.get(TOKEN_KEY));
                Authentication authentication = tokenProvider.getAuthentication(values.get(EMAIL_KEY), grantedAuthorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch(Exception e) {
            log.error(e.getMessage());
            processError(request, response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getHeader(AUTHORIZATION) == null
                || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                || request.getHeader(AUTHORIZATION).equalsIgnoreCase(HTTP_OPTIONS_METHOD)
                || asList(PUBLIC_ROUTES).contains(request.getRequestURI());
    }

    private Map<String, String> getRequestValues(HttpServletRequest request) {
        String token = getToken(request);
        return Map.of(EMAIL_KEY, tokenProvider.getSubject(token, request), TOKEN_KEY, token);
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(header -> header.replaceAll(TOKEN_PREFIX, EMPTY))
                .get();
    }
}
