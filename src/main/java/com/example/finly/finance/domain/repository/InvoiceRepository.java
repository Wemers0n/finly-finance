package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.CreditCard;
import com.example.finly.finance.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByCreditCardIdOrderByReferenceMonthDesc(CreditCard creditCard);
}
