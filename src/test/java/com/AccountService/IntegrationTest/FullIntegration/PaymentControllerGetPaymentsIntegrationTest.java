package com.AccountService.IntegrationTest.FullIntegration;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.entity.PaymentEntity;
import com.AccountService.entity.UserEntity;
import com.AccountService.repository.PaymentRepository;
import com.AccountService.repository.UserRepository;
import com.AccountService.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class PaymentControllerGetPaymentsIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentService paymentService;
    PaymentDTO paymentDTO1;
    PaymentDTO paymentDTO2;
    PaymentDTO paymentDTO3;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRepository.save(new UserEntity("Jakub", "Kaiser", "kuba1@acme.com", "123456", "ROLE_USER"));
        userRepository.save(new UserEntity("Jakub", "Kaiser", "kuba2@acme.com", "123456", "ROLE_USER"));
        paymentDTO1 = new PaymentDTO("kuba1@acme.com", "01-2022", 123);
        paymentDTO2 = new PaymentDTO("kuba1@acme.com", "02-2022", 123);
        paymentDTO3 = new PaymentDTO("kuba2@acme.com", "01-2022", 123);
        paymentService.addPayments(List.of(paymentDTO1, paymentDTO2, paymentDTO3));
//        paymentRepository.save(
//                new PaymentEntity("kuba1@acme.com", "01-2022", 123));
//        paymentRepository.save(
//                new PaymentEntity("kuba1@acme.com", "02-2022", 123));
//        paymentRepository.save(
//                new PaymentEntity("kuba2@acme.com", "01-2022", 123));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ACCOUNTANT"})
    void shouldReturnAllPayments() throws Exception{
        List<PaymentDTO> returnPayments = List.of(paymentDTO2, paymentDTO1, paymentDTO3);
        String expectedReturnJson = objectMapper.writeValueAsString(returnPayments);
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReturnJson));

    }

    @Test
    @WithMockUser(roles = {"ACCOUNTANT"})
    void shouldReturnPaymentsForSingleUser() throws Exception{
        List<PaymentDTO> expectedReturnPayments = List.of(paymentDTO1,paymentDTO3);
        String expectedReturnJson = objectMapper.writeValueAsString(expectedReturnPayments);
        mockMvc.perform(get("/payments")
                        .param("period", "01-2022"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReturnJson));
    }

    @Test
    @WithMockUser(roles = {"ACCOUNTANT"})
    void shouldReturnPaymentsForSpecificPeriod() throws Exception{
        List<PaymentDTO> expectedReturnPayments = List.of(paymentDTO2,paymentDTO1);
        String expectedReturnJson = objectMapper.writeValueAsString(expectedReturnPayments);
        mockMvc.perform(get("/payments")
                        .param("username", "kuba1@acme.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReturnJson));
    }

    @Test
    @WithMockUser(roles = {"ACCOUNTANT"})
    void shouldReturnPaymentsForSpecificPeriodAndUser() throws Exception{
        String expectedReturnJson = objectMapper.writeValueAsString(paymentDTO1);
        mockMvc.perform(get("/payments")
                        .param("username", "kuba1@acme.com")
                        .param("period", "01-2022"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReturnJson));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnForbidden() throws Exception{
        mockMvc.perform(get("/payments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "kuba1@acme.com")
    void shouldReturnPaymentsForLoggedUser() throws Exception{
        List<PaymentDTO> expectedReturnPayments = List.of(paymentDTO2,paymentDTO1);
        String expectedReturnJson = objectMapper.writeValueAsString(expectedReturnPayments);
        mockMvc.perform(get("/my/payments"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedReturnJson));
    }


}
