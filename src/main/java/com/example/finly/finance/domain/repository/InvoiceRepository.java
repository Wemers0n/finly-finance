package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.CreditCard;
import com.example.finly.finance.domain.model.Invoice;
import com.example.finly.finance.domain.model.enums.EInvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.transactions WHERE i.creditCardId = :creditCard ORDER BY i.referenceMonth DESC")
    List<Invoice> findByCreditCardIdOrderByReferenceMonthDesc(@Param("creditCard") CreditCard creditCard);

    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.transactions WHERE i.creditCardId IN :cards AND i.status = :status")
    List<Invoice> findByCreditCardIdInAndStatus(@Param("cards") List<CreditCard> cards, @Param("status") EInvoiceStatus status);
}
