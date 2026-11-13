package org.example.dto;

import java.math.BigDecimal;

public class TransferRequest {

    private int senderAccount;
    private int recipientAccount;
    private BigDecimal amount;

    public TransferRequest() {}

    public int getSenderAccount() {
        return senderAccount;
    }

    public int getRecipientAccount() {
        return recipientAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setSenderAccount(int newSenderAccount) {
        this.senderAccount = newSenderAccount;
    }

    public void setRecipientAccount(int newRecipientAccount) {
        this.recipientAccount = newRecipientAccount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
