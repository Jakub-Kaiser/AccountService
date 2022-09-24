package com.AccountService.UnitTest;

import com.AccountService.DTO.UserDTO;
import com.AccountService.controller.AccountServiceController;
import com.AccountService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerUnitTest {

    AccountServiceController controller;

    @Mock
    UserService userService;

    Map<String, String> returnUser;
    UserDTO correctUserDTO;
    UserDTO incorrectUserDTO;
    UserDTO expectedReturnUserDTO;

    @BeforeEach
    void setUp() {
        controller = new AccountServiceController(userService);
        correctUserDTO = new UserDTO("Jakub", "Kaiser", "kuba@acme.com", "123");
        incorrectUserDTO = new UserDTO("Jakub", "Kaiser", "kuba@gmail.com", "123");
        expectedReturnUserDTO = new UserDTO(0L, "Jakub", "Kaiser", "kuba@acme.com", "123");
    }

    @Test
    void shouldReturnStatusOK() {
//        assertEquals(HttpStatus.OK, controller.register(correctUserDTO).getStatusCode());
    }


    @Test
    void shouldReturnOKResponse() {
        when(userService.saveUser(correctUserDTO)).thenReturn(expectedReturnUserDTO);
        ResponseEntity<UserDTO> expectedResponse = new ResponseEntity<>(expectedReturnUserDTO, HttpStatus.OK);
        assertEquals(expectedResponse, controller.register(correctUserDTO));
    }


}
