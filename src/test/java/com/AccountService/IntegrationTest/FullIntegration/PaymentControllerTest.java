package com.AccountService.IntegrationTest.FullIntegration;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.entity.PaymentEntity;
import com.AccountService.entity.UserEntity;
import com.AccountService.repository.PaymentRepository;
import com.AccountService.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(new UserEntity(
                "Jakub", "Kaiser", "kuba1@acme.com", "123456", "ROLE_USER"));
        userRepository.save(new UserEntity(
                "Jakub", "Kaiser", "kuba2@acme.com", "123456", "ROLE_USER"));
        paymentRepository.save(new PaymentEntity(
                "kuba1@acme.com", "03-2022", 123
        ));
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "kuba@acme.com", roles = {"ACCOUNTANT"})
    public void shouldAddThreePayments() throws Exception {
        List<PaymentDTO> payments = List.of(
                new PaymentDTO("kuba1@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "02-2022", 1234)
        );
        String json = objectMapper.writeValueAsString(payments);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Payments updated: 3"));
    }

    @Test
    @WithMockUser(username = "kuba@acme.com", roles = {"ACCOUNTANT"})
    public void shouldRespondPaymentExists() throws Exception {
        List<PaymentDTO> payments = List.of(
                new PaymentDTO("kuba1@acme.com", "03-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "02-2022", 1234)
        );
        String json = objectMapper.writeValueAsString(payments);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException().getMessage().contains(
                                "Following payment already exists: User: kuba1@acme.com. Period: 03-2022"
                        )));

    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void whenAddPaymentshouldReturnForbidden() throws Exception{
        List<PaymentDTO> payments = List.of(
                new PaymentDTO("kuba1@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "02-2022", 1234)
        );
        String json = objectMapper.writeValueAsString(payments);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void whenUpdatePaymentshouldReturnForbidden() throws Exception{
        PaymentDTO paymentDTO = new PaymentDTO(
                "kuba1@acme.com", "03-2022", 12345);
        String json = objectMapper.writeValueAsString(paymentDTO);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "kuba@acme.com", roles = {"ACCOUNTANT"})
    public void shouldRespondUserDoesntExist() throws Exception {
        List<PaymentDTO> payments = List.of(
                new PaymentDTO("kuba1@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba2@acme.com", "01-2022", 1234),
                new PaymentDTO("kuba3@acme.com", "02-2022", 1234)
        );
        String json = objectMapper.writeValueAsString(payments);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "User kuba3@acme.com does not exist",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "kuba@acme.com", roles = {"ACCOUNTANT"})
    public void shouldUpdatePayment() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO(
                "kuba1@acme.com", "03-2022", 12345);
        String json = objectMapper.writeValueAsString(paymentDTO);
        mockMvc.perform(put("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(
                        "Payment updated",
                        result.getResponse().getContentAsString()));
    }

    @Test
    @WithMockUser(username = "kuba@acme.com", roles = {"ACCOUNTANT"})
    public void shouldRespondPaymentDoesntExist() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO(
                "kuba1@acme.com", "07-2022", 12345);

        String json = objectMapper.writeValueAsString(paymentDTO);
        mockMvc.perform(put("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "400 BAD_REQUEST \"Following payment doesn't exist: User: kuba1@acme.com. Period: 07-2022\"",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "kuba@acme.com", roles = {"ACCOUNTANT"})
    public void whenUpdatePaymentshouldRespondUserDoesntExist() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO(
                "kuba7@acme.com", "03-2022", 12345);

        String json = objectMapper.writeValueAsString(paymentDTO);
        mockMvc.perform(put("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "User kuba7@acme.com does not exist",
                        result.getResolvedException().getMessage()));
    }


}
