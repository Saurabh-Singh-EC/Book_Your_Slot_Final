package com.codeWithSrb.bookyourslot.service;

import com.codeWithSrb.bookyourslot.model.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.time.LocalDateTime.now;

@Service
public class ResponseService {

    private ResponseEntity<HttpResponse> buildResponse(HttpStatus status,
                                                       String message,
                                                       String reason,
                                                       String developerMessage,
                                                       Map<String, Object> data) {
        return ResponseEntity.status(status)
                .body(HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(status)
                        .statusCode(status.value())
                        .message(message)
                        .reason(reason)
                        .developerMessage(developerMessage)
                        .data(data)
                        .build());
    }
}