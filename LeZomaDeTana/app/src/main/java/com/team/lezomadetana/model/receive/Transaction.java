package com.team.lezomadetana.model.receive;

import java.util.Date;

public class Transaction {
    private Date creationTime;
    private Date updateTime;
    private String senderId;
    private String recipientId;
    private Float amount;
    private String refererId;
    private String type;
    private String description;
    private Boolean processed;

    public Transaction() {
    }

    public Transaction(Date creationTime, Date updateTime, String senderId, String recipientId, Float amount, String refererId, String type, String description, Boolean processed) {
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.refererId = refererId;
        this.type = type;
        this.description = description;
        this.processed = processed;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public Float getAmount() {
        return amount;
    }

    public String getRefererId() {
        return refererId;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public void setRefererId(String refererId) {
        this.refererId = refererId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}
