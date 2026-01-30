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
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankAccount> bankAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditCard> creditCards = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets = new ArrayList<>();


    public User(String firstname, String lastname, String email, String password){
        this.firstname = Objects.requireNonNull(firstname);
        this.lastname = Objects.requireNonNull(lastname);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
    }

    public BankAccount addBankAccount(String accountName, EAccountType accountType, BigDecimal initialBalance){
        validateAccountName(accountName);
        validateInitialBalance(accountType, initialBalance);

        BankAccount account = new BankAccount(this, accountName, accountType, normalizeBalance(initialBalance));
        bankAccounts.add(account);
        return account;
    }

    public Category addCategory(String name) {
        validateCategoryName(name);
        validateCategoryAlreadyExists(name);

        Category category = new Category(this, name);
        categories.add(category);
        return category;
    }

    public Optional<Category> findCategoryByName(String categoryName) {
        if (!(categoryName == null || categoryName.isBlank())) {
            return categories.stream()
                    .filter(category -> category.getName().equalsIgnoreCase(categoryName))
                    .findFirst();
        }

        return Optional.empty();
    }

    private void validateCategoryName(String name) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Nome da categoria é obrigatório");
        }
    }

    private void validateCategoryAlreadyExists(String name) {
        boolean exists = categories.stream()
                .anyMatch(category -> category.getName().equalsIgnoreCase(name));

        if (exists) {
            throw new RuntimeException("Categoria já existe para este usuário");
        }
    }

    public Optional<CreditCard> findCardById(UUID id){
        if (!(id == null || id.toString().isBlank())){
            return creditCards.stream()
                    .filter(card -> card.getId().equals(id))
                    .findFirst();
        }
        return Optional.empty();
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
