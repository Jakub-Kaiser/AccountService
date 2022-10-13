package com.AccountService.controller;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class PaymentController {

    PaymentService paymentService;

    @PostMapping("/payments")
    public String postPayments(@RequestBody @Valid List<PaymentDTO> paymentDTOS) {
        return paymentService.addPayments(paymentDTOS);
    }

    @PutMapping("/payments")
    public String putPayment(@RequestBody @Valid PaymentDTO paymentDTO) {
        return paymentService.updatePayment(paymentDTO);
    }

    @GetMapping("/payments")
    public Object getPayments(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String period) {
        if (username == null && period == null) {
            return paymentService.getAllPayments();
        } else if (period == null) {
            return paymentService.getAllPaymentsByUser(username);
        } else if (username == null) {
            return paymentService.getAllPaymentsByPeriod(period);
        } else {
            return paymentService.getPaymentByUserAndPeriod(username, period);
        }
    }

    @GetMapping("/my/payments")
    public Object getMyPayments(@RequestParam(required = false) String period) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (period == null) {
            return paymentService.getAllPaymentsByUser(username);
        } else {
            return paymentService.getPaymentByUserAndPeriod(username, period);
        }
    }


}
