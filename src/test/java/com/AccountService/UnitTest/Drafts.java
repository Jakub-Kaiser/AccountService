package com.AccountService.UnitTest;

import org.junit.jupiter.api.Test;

import java.time.Year;
import java.time.YearMonth;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Drafts {

    YearMonth yearMonth = YearMonth.of(2022, 10);

    @Test
    void test() {

        String dateString = String.format("%s-%s", yearMonth.getMonthValue(),yearMonth.getYear());
        System.out.println(dateString);
    }

    @Test
    void stringToYearMonth() {
        String stringDate = "01-2022";
        int month = Integer.parseInt(stringDate.substring(0, stringDate.indexOf("-")));
        int year = Integer.parseInt(stringDate.substring(stringDate.indexOf("-")+1));
        YearMonth yearMonth = YearMonth.of(year, month);
        System.out.println(yearMonth.toString());
    }

    @Test
    void testMonthYearRegex() {
        String regex = "(([0][1-9])|([1][0-2]))";
        Scanner scanner = new Scanner(System.in);
        assertTrue("01".matches(regex));
        assertTrue("02".matches(regex));
        assertTrue("03".matches(regex));
        assertTrue("04".matches(regex));
        assertTrue("05".matches(regex));
        assertTrue("06".matches(regex));
        assertTrue("07".matches(regex));
        assertTrue("08".matches(regex));
        assertTrue("09".matches(regex));
        assertTrue("10".matches(regex));
        assertTrue("11".matches(regex));
        assertTrue("12".matches(regex));
        assertFalse("13".matches(regex));


    }

}
