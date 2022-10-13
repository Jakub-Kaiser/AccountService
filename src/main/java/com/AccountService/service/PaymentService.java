package com.AccountService.service;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.entity.PaymentEntity;
import com.AccountService.entity.UserEntity;
import com.AccountService.exception.UserExistsException;
import com.AccountService.repository.PaymentRepository;
import com.AccountService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        PaymentEntity paymentEntity = returnPaymentEntityOrThrow(email, periodString);
        paymentEntity.setSalary(paymentDTO.getSalary());
        paymentRepository.save(paymentEntity);
        return "Payment updated";
    }

    public List<PaymentDTO> getAllPaymentsByUser(String username) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByEmployeeOrderByPeriodDesc(username);
        List<PaymentDTO> paymentDTOs = new ArrayList<>();
        for (PaymentEntity paymentEntity : paymentEntities) {
            paymentDTOs.add(new PaymentDTO(
                    paymentEntity.getEmployee(),
                    paymentEntity.getPeriod(),
                    paymentEntity.getSalary()
            ));
        }

        return paymentDTOs;
    }

    public PaymentDTO getPaymentByUserAndPeriod(String username, String period) {
        if (!userRepository.existsByEmailIgnoreCase(username)) {
            throw new UserExistsException(String.format("User %s does not exist", username));
        }
        PaymentEntity paymentEntity = returnPaymentEntityOrThrow(username, period);
        return new PaymentDTO(
                paymentEntity.getEmployee(),
                paymentEntity.getPeriod(),
                paymentEntity.getSalary());
    }

    private PaymentEntity returnPaymentEntityOrThrow(String username, String period) {
        return paymentRepository.findByEmployeeAndPeriod(username, period).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Following payment doesn't exist: User: %s. Period: %s", username, period)));
    }



    private YearMonth stringToYearMonth(String stringDate) {
        int month = Integer.parseInt(stringDate.substring(0, stringDate.indexOf("-")));
        int year = Integer.parseInt(stringDate.substring(stringDate.indexOf("-")+1));
        return YearMonth.of(year, month);
    }

    public List<PaymentDTO> getAllPayments() {
        List<PaymentEntity> paymentEntities = paymentRepository.findByOrderByPeriodDesc();
        List<PaymentDTO> paymentDTOS = new ArrayList<>();
        for (PaymentEntity paymentEntity : paymentEntities) {
            paymentDTOS.add(new PaymentDTO(
                    paymentEntity.getEmployee(),
                    paymentEntity.getPeriod(),
                    paymentEntity.getSalary()
            ));
        }
        return paymentDTOS;
    }

    public List<PaymentDTO> getAllPaymentsByPeriod(String period) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByPeriodOrderByEmployeeAsc(period);
        List<PaymentDTO> paymentDTOS = new ArrayList<>();
        for (PaymentEntity paymentEntity : paymentEntities) {
            paymentDTOS.add(new PaymentDTO(
                    paymentEntity.getEmployee(),
                    paymentEntity.getPeriod(),
                    paymentEntity.getSalary()
            ));
        }
        return paymentDTOS;
    }
}
