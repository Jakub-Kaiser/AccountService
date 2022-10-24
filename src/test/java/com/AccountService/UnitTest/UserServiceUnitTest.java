package com.AccountService.UnitTest;

import com.AccountService.DTO.UserDTO;
import com.AccountService.exception.UserNotFoundException;
import com.AccountService.repository.UserRepository;
import com.AccountService.security.BreachedPasswords;
import com.AccountService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Autowired
    UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    BreachedPasswords breachedPasswords;


    PasswordEncoder passwordEncoder;
    UserDTO userDTO;
    UserDTO expectedReturnUserDTO;

    @BeforeEach
    void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(passwordEncoder, userRepository, breachedPasswords);
//        breachedPasswords = new BreachedPasswords();
        userDTO = new UserDTO("Jakub", "Kaiser", "kuba@acme.com", "123","ROLE_USER");
        expectedReturnUserDTO = new UserDTO(0L, "Jakub", "Kaiser", "kuba@acme.com", "123","ROLE_USER");
    }

    @Test
    void testSaveUserBreachedPassword() {
        when(breachedPasswords.isPasswordBreached(any()))
                .thenReturn(true);
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userService.saveUser(userDTO);
        });
    }

    @Test
    void shouldReturnNewUser() {
        UserDTO returnUser = userService.saveUser(userDTO);
        assertThat(returnUser).usingRecursiveComparison().isEqualTo(expectedReturnUserDTO);
    }

    @Test
    void shouldSaveUser() {
        when(userRepository.existsByEmailIgnoreCase(userDTO.getEmail())).thenReturn(false);
        userService.saveUser(userDTO);
        verify(userRepository).save(any());
    }

    @Test
    void shouldThrowUserExistsException() {
        when(userRepository.existsByEmailIgnoreCase(userDTO.getEmail())).thenReturn(true);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.saveUser(userDTO);
        });
        String expectedMessage = "User exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }




}
