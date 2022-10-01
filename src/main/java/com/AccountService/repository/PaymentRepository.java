package com.AccountService.repository;

import com.AccountService.DTO.PaymentDTO;
import com.AccountService.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query
    public boolean existsByEmployeeAndPeriod(String employee, String period);

    @Query
    List<PaymentEntity> findByEmployeeOrderByPeriodDesc(String employee);

    @Query
    Optional<PaymentEntity> findByEmployeeAndPeriod(String employee, String period);






}
