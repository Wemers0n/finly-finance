package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EAccountType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tb_users")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 50)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String lastname;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccount> bankAccounts = new ArrayList<>();

    public User(String firstname, String lastname, String email, String password){
        this.firstname = Objects.requireNonNull(firstname);
        this.lastname = Objects.requireNonNull(lastname);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
    }

    public UUID addBankAccount(String accountName, EAccountType accountType, BigDecimal initialBalance){
        validateAccountName(accountName);
        validateInitialBalance(accountType, initialBalance);

        BankAccount account = new BankAccount(this, accountName, accountType, normalizeBalance(initialBalance));
        bankAccounts.add(account);
        return account.getId();
    }

    public Optional<BankAccount> findBankAccountById(UUID accountId){
        return Optional.ofNullable(accountId)
                .flatMap(id -> bankAccounts.stream()
                        .filter(account -> account.getId().equals(id))
                        .findFirst());
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private void validateAccountName(String accountName){
        if (accountName == null || accountName.isBlank()){
            throw new RuntimeException("Nome da conta é obrigatório");
        }
    }

    private void validateInitialBalance(EAccountType accountType, BigDecimal initialBalance){
        if (initialBalance == null) return;

        if (!accountType.allowsNegativeBalance() && initialBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException("Saldo negativo para esse tipo de conta não é permitido");
        }
    }

    private BigDecimal normalizeBalance(BigDecimal balance) {
        return balance != null ? balance : BigDecimal.ZERO;
    }
}
