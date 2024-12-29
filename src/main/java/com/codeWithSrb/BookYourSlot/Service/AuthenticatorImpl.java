package com.codeWithSrb.BookYourSlot.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.codeWithSrb.BookYourSlot.util.ExceptionUtils.processError;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@Component
@Slf4j
public class AuthenticatorImpl implements Authenticator {

    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public AuthenticatorImpl(AuthenticationManager authenticationManager,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        this.authenticationManager = authenticationManager;
        this.request = request;
        this.response = response;
    }

    @Override
    public Authentication authenticate(String email, String password) {
        try {
            log.info("User authentication has started with name {}", email);
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            log.info("User has been authenticated successfully");
            return authentication;
        } catch (Exception exception) {
            processError(request, response, exception);
            throw exception;
        }
    }
}