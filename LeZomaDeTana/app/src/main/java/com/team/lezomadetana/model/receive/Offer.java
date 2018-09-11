package com.team.lezomadetana.model.receive;

import java.util.Date;

/**
 * Created by Hery Andoniana on 08/09/2018.
 **/

public class Offer {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String id;
    private Date creationTime;
    private Date updateTime;
    private String requestId;
    private String userId;
    private Integer quantity;
    private Request.UnitType unitType;
    private Float price;
    private Boolean accepted;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Offer() {
    }

    public Offer(String id, Date creationTime, Date updateTime, String requestId, String userId, Integer quantity, Request.UnitType unitType, Float price, Boolean accepted) {
        this.id = id;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.requestId = requestId;
        this.userId = userId;
        this.quantity = quantity;
        this.unitType = unitType;
        this.price = price;
        this.accepted = accepted;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public String getId() {
        return id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
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

    public Float getPrice() {
        return price;
    }

    public Boolean getAccepted() {
        return accepted;
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
