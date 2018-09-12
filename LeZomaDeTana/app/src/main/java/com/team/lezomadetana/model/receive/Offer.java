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
    private User user;
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

    public Offer(String id, User user, Date creationTime, Date updateTime, String requestId, String userId, Integer quantity, Request.UnitType unitType, Float price, Boolean accepted) {
        this.id = id;
        this.user = user;
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

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Request.UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(Request.UnitType unitType) {
        this.unitType = unitType;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getAccepted() {
        return accepted;
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
