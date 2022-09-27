package com.AccountService.repository;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query
    public boolean existsByEmployeeAndPeriod(String employee, String period);

}
