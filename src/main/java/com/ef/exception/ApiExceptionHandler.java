package com.ef.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class ApiExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<ApiError> handleCustomException(CustomException cex, WebRequest request) {
        ApiError apiError = cex.getApiError();
        apiError.setPath(request.getDescription(false));
        return new ResponseEntity<>(cex.getApiError(), cex.getApiError().getHttpStatus());
    }
}
