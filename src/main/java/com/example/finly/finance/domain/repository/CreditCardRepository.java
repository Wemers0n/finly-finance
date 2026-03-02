package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {

    @Query("""
        select c
        from CreditCard c
        where c.invoiceClosingDay = :day
    """)
    List<CreditCard> findCardsToCloseInvoice(@Param("day") int day);
}
