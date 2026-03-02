package com.example.finly.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinlyFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinlyFinanceApplication.class, args);
    }

}
