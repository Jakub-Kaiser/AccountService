package com.AccountService.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CustomErrorMessage {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private String description;


}
