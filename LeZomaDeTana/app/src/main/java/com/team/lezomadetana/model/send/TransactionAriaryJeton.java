package com.team.lezomadetana.model.send;

public class TransactionAriaryJeton {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String userId;
    private String phone;
    private Float amount;
    private Operator operator;
    private Type type;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TransactionAriaryJeton() {
    }

    public TransactionAriaryJeton(String userId, String phone, Float amount, Operator operator, Type type) {
        this.userId = userId;
        this.phone = phone;
        this.amount = amount;
        this.operator = operator;
        this.type = type;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

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

    public enum Operator {AIRTEL, ORANGE, TELMA}

    public enum Type {DEPOSIT, WITHDRAWAL}

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
