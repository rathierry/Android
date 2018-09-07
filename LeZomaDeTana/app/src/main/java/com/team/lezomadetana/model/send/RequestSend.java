package com.team.lezomadetana.model.send;

import com.team.lezomadetana.model.receive.Request;

public class RequestSend {

    private String userId;
    private String product;

    private Request.UnitType unitType;
    private Float price;
    private Request.Type type;
    private String templateId;

    public RequestSend(String userId, String product, Request.UnitType unitType, Float price, Request.Type type, String templateId) {
        this.userId = userId;
        this.product = product;
        this.unitType = unitType;
        this.price = price;
        this.type = type;
        this.templateId = templateId;
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

    public Float getPrice() {
        return price;
    }

    public Request.Type getType() {
        return type;
    }

    public String getTemplateId() {
        return templateId;
    }
}
