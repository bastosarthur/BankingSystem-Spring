package org.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FindTransactionResponse {

    private BigDecimal amount;
    private int id;
    private Integer recipient;
    private Integer sender;
    private String status;
    private LocalDateTime dateTime;
    private String type;

    public FindTransactionResponse() {}

    public FindTransactionResponse(BigDecimal amount, int id, Integer recipient, Integer sender, String status,
                                   LocalDateTime dateTime, String type) {
        this.amount = amount;
        this.id = id;
        this.recipient = recipient;
        this.sender = sender;
        this.status = status;
        this.dateTime = dateTime;
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public Integer getSender() {
        return sender;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getType() {
        return type;
    }

    public void setAmount(BigDecimal newAmount) {
        this.amount = newAmount;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public void setRecipient(Integer newRecipient) {
        this.recipient = newRecipient;
    }

    public void setSender(Integer newSender) {
        this.sender = newSender;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public void setDateTime(LocalDateTime newDateTime) {
        this.dateTime = newDateTime;
    }

    public void setType(String newType) {
        this.type = newType;
    }
}
