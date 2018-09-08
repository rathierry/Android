package com.team.lezomadetana.model.send;

import com.team.lezomadetana.model.receive.Request;

public class OfferSend
{
    private String requestId;
    private String userId;
    private Integer quantity;
    private Request.UnitType unitType;
    private Boolean accepted;


    public OfferSend() {
    }

    public OfferSend(String requestId, String userId, Integer quantity, Request.UnitType unitType, Boolean accepted) {
        this.requestId = requestId;
        this.userId = userId;
        this.quantity = quantity;
        this.unitType = unitType;
        this.accepted = accepted;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Request.UnitType getUnitType() {
        return unitType;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitType(Request.UnitType unitType) {
        this.unitType = unitType;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
