package com.codeWithSrb.BookYourSlot.Model;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class HttpResponse {

    protected String timeStamp;
    protected int statusCode;
    protected HttpStatus httpStatus;
    protected String message;
    protected String reason;
    protected String developerMessage;
    protected Map<?, ?> data;

    public HttpResponse() {
    }

    public HttpResponse(HttpResponseBuilder httpResponseBuilder) {
        this.timeStamp = httpResponseBuilder.timeStamp;
        this.statusCode = httpResponseBuilder.statusCode;
        this.httpStatus = httpResponseBuilder.httpStatus;
        this.message = httpResponseBuilder.message;
        this.reason = httpResponseBuilder.reason;
        this.developerMessage = httpResponseBuilder.developerMessage;
        this.data = httpResponseBuilder.data;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public Map<?, ?> getData() {
        return data;
    }

    public void setData(Map<?, ?> data) {
        this.data = data;
    }

    public static class HttpResponseBuilder {
        private String timeStamp;
        private int statusCode;
        private HttpStatus httpStatus;
        private String message;
        private String reason;
        private String developerMessage;
        private Map<?, ?> data;

        public HttpResponseBuilder withTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public HttpResponseBuilder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public HttpResponseBuilder withHttpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public HttpResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public HttpResponseBuilder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public HttpResponseBuilder withDevelopersMessage(String developerMessage) {
            this.developerMessage = developerMessage;
            return this;
        }

        public HttpResponseBuilder withData(Map<?, ?> data) {
            this.data = data;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
