package com.AccountService.IntegrationTest.FullIntegration;

import com.AccountService.DTO.UserDTO;
import com.AccountService.exception.UserNotFoundException;
import com.AccountService.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerIntTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;
    UserDTO returnUserWithId;
    Map<String, String> inputUser;
    Map<String, String> inputUserWrongEmail;
    MvcResult initialResult;
    ObjectMapper objectMapper = new ObjectMapper();
    String inputJson;


    @BeforeEach
    void setUp() throws Exception {
        inputUser = new LinkedHashMap<>();
        inputUserWrongEmail = new LinkedHashMap<>();
        inputUser.put("name", "Jakub");
        inputUser.put("lastname", "Kaiser");
        inputUser.put("email", "kuba@acme.com");
        inputUser.put("password", "111111111111");
        inputUserWrongEmail.put("name", "Jakub");
        inputUserWrongEmail.put("lastname", "Kaiser");
        inputUserWrongEmail.put("email", "kuba@gmail.com");
        inputUserWrongEmail.put("password", "111111111111");
        returnUserWithId = new UserDTO(0L, "Jakub", "Kaiser", "kuba@acme.com", "123", "ROLE_USER");
        ObjectMapper objectMapper = new ObjectMapper();
        inputJson = objectMapper.writeValueAsString(inputUser);
        initialResult = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson)).andReturn();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldExistUser() throws Exception {
        assertTrue(userRepository.existsByEmailIgnoreCase("kuba@acme.com"));
    }

    @Test
    void shouldNotExistUser() throws Exception {
        assertFalse(userRepository.existsByEmailIgnoreCase("kuba@gmail.com"));
    }

    @Test
    void shouldReturnOkStatus() {
        assertEquals(200, initialResult.getResponse().getStatus());
    }

    @Test
    void shouldReturnJsonWithUser() throws Exception {
        String returnJson = initialResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(returnJson);
        assertEquals("Jakub", jsonObject.getString("name"));
        assertEquals("Kaiser", jsonObject.getString("lastname"));
        assertEquals("kuba@acme.com", jsonObject.getString("email"));
        assertTrue(jsonObject.getString("id").matches("\\d+"));
    }

    @Test
    void shouldRespondThatUserExists() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result ->
                        assertEquals("User exists", result.getResolvedException().getMessage()));
    }

    @Test
    void shouldRespondPasswordInsecure() throws Exception {
        inputUser.put("password", "PasswordForMay");
        inputUser.put("email", "k2@acme.com");
        inputJson = objectMapper.writeValueAsString(inputUser);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result ->
                        assertEquals("400 BAD_REQUEST \"The password is insecure\"", result.getResolvedException().getMessage()));
    }

    @Test
    void shouldRespondPasswordTooShort() throws Exception{
        inputUser.put("password", "123");
        String inputJson = objectMapper.writeValueAsString(inputUser);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result ->
                        assertTrue(result.getResolvedException().getMessage()
                                .contains("Password must be at least 6 characters long")));
    }



    @Test
    void shouldNotAuthorizeUserWrongPassword() throws Exception {
        mockMvc.perform(get("/auth").with(httpBasic("kuba@acme.com","1234")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotAuthorizeUserWrongEmail() throws Exception {
        mockMvc.perform(get("/auth").with(httpBasic("kuba@gmail.com","123")))
                .andExpect(status().isUnauthorized());
    }



}
