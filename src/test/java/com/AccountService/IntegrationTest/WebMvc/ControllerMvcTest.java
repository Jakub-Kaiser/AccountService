package com.AccountService.IntegrationTest.WebMvc;

import com.AccountService.DTO.UserDTO;
import com.AccountService.controller.UserController;
import com.AccountService.exception.UserNotFoundException;
import com.AccountService.security.BreachedPasswords;
import com.AccountService.security.UserDetailsServiceImpl;
import com.AccountService.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class ControllerMvcTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    BreachedPasswords breachedPasswords;
    UserDTO returnUserWithId;
    Map<String, String> inputUser;
    Map<String, String> inputUserWrongEmail;
    Map<String, String> inputUserShortPassword;
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        inputUser = new LinkedHashMap<>();
        inputUserWrongEmail = new LinkedHashMap<>();
        inputUserShortPassword = new LinkedHashMap<>();
        inputUser.put("name", "Jakub");
        inputUser.put("lastname", "Kaiser");
        inputUser.put("email", "kuba@acme.com");
        inputUser.put("password", "111111111111");

        inputUserWrongEmail.put("name", "Jakub");
        inputUserWrongEmail.put("lastname", "Kaiser");
        inputUserWrongEmail.put("email", "kuba@gmail.com");
        inputUserWrongEmail.put("password", "111111111111");

        inputUserShortPassword.put("name", "Jakub");
        inputUserShortPassword.put("lastname", "Kaiser");
        inputUserShortPassword.put("email", "kuba@acme.com");
        inputUserShortPassword.put("password", "123");

        returnUserWithId = new UserDTO(0L, "Jakub", "Kaiser", "kuba@acme.com", "123","ROLE_USER");
    }


    @Test
    void testRegisterOkPath() throws Exception {
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
    void testAddUserWithBreachedPassword() {
//        when(breachedPasswords.isPasswordBreached(anyString()))
//                .thenReturn();

    }

    @Test
    @DisplayName("When \"name\" missing, return 400 and \"name must not be empty\"")
    void testAddUserNameMissing() throws Exception{
        inputUser.put("name", "");
        String inputJson = objectMapper.writeValueAsString(inputUser);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue( result.getResponse().getContentAsString()
                                .contains("\"errors\":[\"name must not be empty\"]")));
    }

    @Test
    @DisplayName("When name missing and password too short" +
            ", should return 400 and include both error messages")
    void testAddUserMissingNameShortPassword() throws Exception {
        inputUser.put("name", "");
        inputUser.put("password", "123");
        String inputJson = objectMapper.writeValueAsString(inputUser);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResponse().getContentAsString()
                                .contains("name must not be empty")))
                .andExpect(result ->
                        assertTrue(result.getResponse().getContentAsString()
                                .contains("Password must be at least 6 characters long")));
    }

    @Test
    @DisplayName("When password too short, should return 400 and relevant message")
    void testPasswordTooShort() throws Exception {
        String inputJson = objectMapper.writeValueAsString(inputUserShortPassword);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException().getMessage()
                                .contains("default message [Password must be at least 6 characters long]")));
    }


    @Test
    void testRegisterWrongEmail() throws Exception {
        String inputJson = objectMapper.writeValueAsString(inputUserWrongEmail);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUserExists() throws Exception {
        String inputJson = objectMapper.writeValueAsString(inputUser);
        when(userService.saveUser(any())).thenThrow(new UserNotFoundException("User exists"));
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result ->
                        assertEquals("User exists", result.getResolvedException().getMessage()));
    }

    @Test
    void whenEmptyJsonShouldRespondBadRequest() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEmptyBodyShouldRespondBadRequest() throws Exception {
        mockMvc.perform(post("/register"))
                .andExpect(status().isBadRequest());
    }
}
