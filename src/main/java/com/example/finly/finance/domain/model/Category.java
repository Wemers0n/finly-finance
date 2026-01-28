package com.example.finly.finance.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_categories")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Category {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO; // campo derivado

    @OneToMany(mappedBy = "categoryId")
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "categoryId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets = new ArrayList<>();

    public Category(User userId, String name){
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId);
        this.name = Objects.requireNonNull(name);
    }

    public UUID addTransaction(Transaction transaction){
        this.transactions.add(transaction);
        this.addTotalSpentValue(transaction.getValue());
        return transaction.getId();
    }

    public void removeTransaction(UUID transactionId) {
        Transaction transaction = this.transactions.stream()
                .filter(ts -> ts.getId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        this.transactions.remove(transaction);
        this.removeTotalSpentValue(transaction.getValue());
    }

    private void addTotalSpentValue(BigDecimal value){
        this.totalSpent = this.totalSpent.add(value);
    }

    private void removeTotalSpentValue(BigDecimal value){
        this.totalSpent = this.totalSpent.subtract(value);
    }
    public UUID addBudget(Budget budget){
        this.budgets.add(budget);
        return budget.getId();
    }

    public void removeBudget(UUID id){
        this.budgets.removeIf(bt -> bt.getId().equals(id));
    }
}
