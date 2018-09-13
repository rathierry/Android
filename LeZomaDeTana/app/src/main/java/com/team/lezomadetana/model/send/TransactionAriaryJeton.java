package com.team.lezomadetana.model.send;

public class TransactionAriaryJeton
{

    private String userId;
    private String phone;
    private Float amount;
    private Operator operator;
    private Type type;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TransactionAriaryJeton(String userId, String phone, Float amount, Operator operator, Type type) {
        this.userId = userId;
        this.phone = phone;
        this.amount = amount;
        this.operator = operator;
        this.type = type;
    }

    public TransactionAriaryJeton() {
    }

    public enum Operator{ AIRTEL, ORANGE, TELMA}
    public enum Type{DEPOSIT,WITHDRAWAL}

}
