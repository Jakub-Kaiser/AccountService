package com.AccountService.IntegrationTest.FullIntegration;

import com.AccountService.DTO.UserDTO;
import com.AccountService.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceIntTest {

    @Autowired
    UserService userService;
    UserDTO userDTO = new UserDTO("Jakub", "Kaiser", "k@acme.com", "PasswordForMay");

    @Test
    void testPasswordBreached() {
        Exception thrown = assertThrows(ResponseStatusException.class,
                () -> userService.saveUser(userDTO));
        assertEquals("400 BAD_REQUEST \"The password is insecure\"",thrown.getMessage());
    }

    @Test
    void throwIfPasswordBreached() {
        Exception thrown = assertThrows(ResponseStatusException.class,
                () -> userService.throwIfPasswordBreached(userDTO.getPassword()));
        assertEquals("400 BAD_REQUEST \"The password is insecure\"",thrown.getMessage());
    }





}
