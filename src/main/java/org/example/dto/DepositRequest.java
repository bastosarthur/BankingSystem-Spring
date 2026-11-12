package org.example.dto;

import java.math.BigDecimal;

public class DepositRequest {

    private int accountNumber;

    private BigDecimal amount;

    public int getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAccountNumber(int newAccountNumber) {
        this.accountNumber = newAccountNumber;
    }

    public void setAmount(BigDecimal newAmount) {
        this.amount = newAmount;
    }
}
