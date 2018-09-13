package com.team.lezomadetana.model.send;

public  class TransactionSend{

     private String   senderId;
     private String   recipientId;
     private Float   amount;
     private String description;
     private Status status;


    public TransactionSend() {
    }

    public TransactionSend(String senderId, String recipientId, Float amount, String description, Status status) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status{VALIDATED, PENDING, REJECTED}
}


