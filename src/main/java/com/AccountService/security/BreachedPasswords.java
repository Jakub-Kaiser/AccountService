package com.AccountService.security;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BreachedPasswords {

    private List<String> breachedPasswords = List.of(
            "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    );

    public boolean isPasswordBreached(String password) {
        return breachedPasswords.contains(password);
    }

}
