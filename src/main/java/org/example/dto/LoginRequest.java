package org.example.dto;

public class LoginRequest {

    private int accountNumber;

    private String password;

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setAccountNumber(int newAccountNumber) {
        this.accountNumber = newAccountNumber;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
