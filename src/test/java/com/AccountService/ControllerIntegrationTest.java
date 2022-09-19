package com.AccountService;


import com.AccountService.controller.AccountServiceController;
import com.AccountService.controller.TestController;
import com.AccountService.security.UserDetailsServiceImpl;
import com.AccountService.security.WebSecurityConfig;
import com.AccountService.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)


public class ControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    void test() throws Exception {
        mockMvc.perform(post("/test").content("Kuba"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Kuba"));
    }

}
