package com.AccountService.exception;

import org.apache.catalina.User;
import org.apache.catalina.webresources.JarResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    //for practice purpose
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<CustomErrorMessage> handleUserExistsException(
            UserExistsException e, WebRequest request) {

        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        int errorCount = 1;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now());
        body.put("exception", ex.getClass());
        List<FieldError> validationErrors = ex.getBindingResult().getFieldErrors();
        String[] errorMessages = new String[validationErrors.size()];
        for (int i = 0; i < validationErrors.size(); i++) {
            if (validationErrors.get(i).getDefaultMessage().equals("must not be empty")) {
                errorMessages[i] = validationErrors.get(i).getField() + " must not be empty";
            } else {
                errorMessages[i] = validationErrors.get(i).getDefaultMessage();
            }
        }
        body.put("errors", errorMessages);

//        body.put("message", Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
        return new ResponseEntity<>(body, headers, status);
    }


}