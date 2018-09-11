package com.team.lezomadetana.model.send;

import com.team.lezomadetana.model.receive.Request;

/**
 * Created by Hery Andoniana on 08/09/2018.
 **/

public class OfferSend {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String requestId;
    private String userId;
    private Integer quantity;
    private Request.UnitType unitType;
    private Boolean accepted;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OfferSend() {
    }

    public OfferSend(String requestId, String userId, Integer quantity, Request.UnitType unitType, Boolean accepted) {
        this.requestId = requestId;
        this.userId = userId;
        this.quantity = quantity;
        this.unitType = unitType;
        this.accepted = accepted;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

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
