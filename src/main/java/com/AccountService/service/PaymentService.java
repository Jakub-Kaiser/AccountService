package com.AccountService.service;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.entity.PaymentEntity;
import com.AccountService.exception.UserExistsException;
import com.AccountService.repository.PaymentRepository;
import com.AccountService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {

    PaymentRepository paymentRepository;
    UserRepository userRepository;

    @Transactional
    public String addPayments(List<PaymentDTO> paymentDTOS) {
        int count = 0;
        for (PaymentDTO paymentDTO : paymentDTOS) {
            String email = paymentDTO.getEmployee();
            String periodString = String.format(
                    "%02d-%d", paymentDTO.getPeriod().getMonthValue(), paymentDTO.getPeriod().getYear()
            );
            //check if employee exists:
            if (!userRepository.existsByEmailIgnoreCase(email)) {
                throw new UserExistsException(String.format("User %s does not exist", email));
            }
            //check if employee-period pair is unique:
            if (paymentRepository
                    .existsByEmployeeAndPeriod(email, periodString)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(
                        "Following payment already exists: User: %s. Period: %s", email, periodString
                ));
            }
            PaymentEntity paymentEntity = new PaymentEntity(
                    email,
                    periodString,
                    paymentDTO.getSalary()
            );
            paymentRepository.save(paymentEntity);
            count++;
        }
        return "Payments updated: " + count;
    }

    public String updatePayment(PaymentDTO paymentDTO) {
        String email = paymentDTO.getEmployee();
        String periodString = String.format(
                "%02d-%d", paymentDTO.getPeriod().getMonthValue(), paymentDTO.getPeriod().getYear()
        );
        //check if employee exists:
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserExistsException(String.format("User %s does not exist", email));
        }
        //check if employee-period pair exists:
        if (!paymentRepository.existsByEmployeeAndPeriod(email, periodString)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(
                    "Following payment doesn't exist: User: %s. Period: %s", email, periodString
            ));
        }
        PaymentEntity paymentEntity = new PaymentEntity(
                email,
                periodString,
                paymentDTO.getSalary()
        );
        paymentRepository.save(paymentEntity);
        return "Payment updated";
    }

    public List<PaymentDTO> getAllUserPayments() {
        return null;
    }
}
