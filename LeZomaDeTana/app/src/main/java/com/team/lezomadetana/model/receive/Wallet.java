package com.team.lezomadetana.model.receive;

public class Wallet
{
    private String userId;

    private Float balance;


    public Wallet() {
    }

    public Wallet(String userId, Float balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public Float getBalance() {
        return balance;
    }
}
