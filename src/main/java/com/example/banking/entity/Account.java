package com.example.banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Account_details")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "initial_balance", nullable = false)
    private BigDecimal initialBalance;

    // Constructors
    public Account() {
    }

    public Account(BigDecimal balance, BigDecimal initialBalance) {
        this.balance = balance;
        this.initialBalance = initialBalance;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
