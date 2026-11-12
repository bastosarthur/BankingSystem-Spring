package org.example.dto;

import java.math.BigDecimal;

public class WithdrawRequest {

    private int accountNumber;

    private BigDecimal amount;

    private WithdrawRequest() {}

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
