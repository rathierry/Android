package com.team.lezomadetana.model.send;

import com.team.lezomadetana.model.receive.Request;

/**
 * Created by Hery Andoniaina on 30/08/2018.
 **/

public class RequestSend {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String userId;
    private String product;
    private Request.UnitType unitType;
    private Integer quantity;
    private Float price;
    private Request.Type type;
    private String templateId;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RequestSend() {
    }

    public RequestSend(String userId, String product, Request.UnitType unitType, Float price, Request.Type type, String templateId, Boolean active) {
        this.userId = userId;
        this.product = product;
        this.unitType = unitType;
        this.price = price;
        this.type = type;
        this.templateId = templateId;
        this.active = active;
    }

    public RequestSend(String userId, String product, Request.UnitType unitType, Integer quantity, Request.Type type, Float price, String templateId, Boolean active) {
        this.userId = userId;
        this.product = product;
        this.unitType = unitType;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.templateId = templateId;
        this.active = active;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public Boolean getActive() {
        return active;
    }

    private Boolean active;

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public String getProduct() {
        return product;
    }

    public Request.UnitType getUnitType() {
        return unitType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public Request.Type getType() {
        return type;
    }

    public String getTemplateId() {
        return templateId;
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
