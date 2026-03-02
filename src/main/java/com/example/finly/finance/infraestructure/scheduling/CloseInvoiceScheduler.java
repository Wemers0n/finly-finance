package com.example.finly.finance.infraestructure.scheduling;

import com.example.finly.finance.domain.services.invoices.CloseInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CloseInvoiceScheduler {

    private final CloseInvoiceService closeInvoiceService;
    private final Clock clock;

    @Scheduled(cron = "0 0 1 * * *")
    public void run() {
        closeInvoiceService.closeInvoices(LocalDate.now(clock));
    }
}
