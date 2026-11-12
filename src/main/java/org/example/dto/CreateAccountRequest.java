package org.example.dto;

import org.example.Account;

public class CreateAccountRequest {

    private Account account;

    private String password;

    public CreateAccountRequest() {}

    public Account getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(Account newAccount) {
        this.account = newAccount;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }


}
