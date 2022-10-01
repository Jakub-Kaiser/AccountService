package com.AccountService.controller;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Calendar;
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
            @RequestParam(required = false) String period) {
        if (period == null) {
            return paymentService.getAllUserPayments();
        } else {
            return paymentService.getPaymentByPeriod(period);
        }
    }

}
