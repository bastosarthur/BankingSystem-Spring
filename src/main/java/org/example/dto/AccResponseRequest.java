package org.example.dto;

import java.math.BigDecimal;

public class AccResponseRequest {

    private int accountNumber;
    private String cpf;
    private BigDecimal balance;

    public AccResponseRequest() {}

    public AccResponseRequest(int accountNumber, String cpf, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.cpf = cpf;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getCpf() {
        return cpf;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccountNumber(int newAccountNumber) {
        this.accountNumber = newAccountNumber;
    }

    public void setCpf(String newCpf) {
        this.cpf = newCpf;
    }

    public void setBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }
}
