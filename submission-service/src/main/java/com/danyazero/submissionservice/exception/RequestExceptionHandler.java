package com.danyazero.submissionservice.exception;

import com.danyazero.submissionservice.model.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RequestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionDto> handleException(RequestException e) {
        return new ResponseEntity<>(
                new ExceptionDto(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
