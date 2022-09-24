package com.AccountService.UnitTest;

import com.AccountService.security.BreachedPasswords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreachedPasswordsUnitTests {

    @Autowired
    BreachedPasswords breachedPasswords;

    @BeforeEach
    void setUp() {
        breachedPasswords = new BreachedPasswords();
    }

    @Test
    void shouldFindPasswordInBreachedPasswords() {
        assertTrue(breachedPasswords.isPasswordBreached("PasswordForMay"));
    }
    @Test
    void shouldNotFindPasswordInBreachedPasswords() {
        assertFalse(breachedPasswords.isPasswordBreached("StrongPassword"));
    }
}
