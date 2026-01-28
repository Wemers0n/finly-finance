package com.example.finly.finance.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_monthly_summaries")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class MonthlySummaries {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccountId;

    @Column(name = "reference_month", nullable = false)
    private LocalDate referenceMonth;

    @Column(name = "total_expense", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalExpense;

    @Column(name = "total_revenue", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "final_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalBalance;
}
