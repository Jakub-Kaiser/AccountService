package com.AccountService.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Year;
import java.time.YearMonth;

@Getter
//@AllArgsConstructor
public class PaymentDTO {
    @NotEmpty(message = "employee must not be empty")
    private String employee;
    @NotNull(message = "period must not be empty")
    @JsonFormat(pattern = "MM-yyyy")
    private YearMonth period;
    @NotNull(message = "salary must not be empty")
    @Min(value = 0, message = "salary must not be negative")
    private long salary;

    public PaymentDTO(String employee, String period, long salary) {
        this.employee = employee;
        int month = Integer.parseInt(period.substring(0, period.indexOf("-")));
        int year = Integer.parseInt(period.substring(period.indexOf("-") + 1));
        this.period = YearMonth.of(year, month);
        this.salary = salary;
    }


}
