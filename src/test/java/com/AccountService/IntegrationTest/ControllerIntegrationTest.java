package com.AccountService.IntegrationTest;

import com.AccountService.DTO.UserDTO;
import com.AccountService.controller.AccountServiceController;
import com.AccountService.exception.UserExistsException;
import com.AccountService.security.UserDetailsServiceImpl;
import com.AccountService.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountServiceController.class)
public class ControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    UserDTO returnUserWithId;
    Map<String, String> inputUser;
    Map<String, String> inputUserWrongEmail;


    @BeforeEach
    void setUp() {
        inputUser = new LinkedHashMap<>();
        inputUserWrongEmail = new LinkedHashMap<>();
        inputUser.put("name", "Jakub");
        inputUser.put("lastname", "Kaiser");
        inputUser.put("email", "kuba@acme.com");
        inputUser.put("password", "123");

        inputUserWrongEmail.put("name", "Jakub");
        inputUserWrongEmail.put("lastname", "Kaiser");
        inputUserWrongEmail.put("email", "kuba@gmail.com");
        inputUserWrongEmail.put("password", "123");

        returnUserWithId = new UserDTO(0L,"Jakub", "Kaiser", "kuba@acme.com", "123");
    }

    @Test
    @WithMockUser
    void testAuthorizedUser() throws Exception {
        mockMvc.perform(get("/auth")).andExpect(status().isOk());
    }

    @Test
    void testUnAuthorizedUser() throws Exception {
        mockMvc.perform(get("/auth")).andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterOkPath() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputJson = objectMapper.writeValueAsString(inputUser);
        when(userService.saveUser(any())).thenReturn(returnUserWithId);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
//                .andExpect(content().string(returnUserJson));
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jakub"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("Kaiser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("kuba@acme.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("0"));

    }

    @Test
    void testRegisterWrongEmail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputJson = objectMapper.writeValueAsString(inputUserWrongEmail);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUserExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputJson = objectMapper.writeValueAsString(inputUser);
        when(userService.saveUser(any())).thenThrow(new UserExistsException("User exists"));
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserExistsException))
                .andExpect(result ->
                        assertEquals("User exists", result.getResolvedException().getMessage()));
    }
}
