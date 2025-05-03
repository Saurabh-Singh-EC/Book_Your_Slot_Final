package com.codeWithSrb.bookyourslot.config;

import com.codeWithSrb.bookyourslot.model.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private static final String AUTHENTICATION_ERROR = "Please login to access this resource.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(now().toString())
                .reason(AUTHENTICATION_ERROR)
                .statusCode(UNAUTHORIZED.value())
                .httpStatus(UNAUTHORIZED)
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}