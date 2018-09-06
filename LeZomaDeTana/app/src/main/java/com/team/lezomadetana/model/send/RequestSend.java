package com.team.lezomadetana.model.send;

import com.team.lezomadetana.model.receive.Request;

public class RequestSend {

    private String userId;
    private String product;

    private String unitType;
    private Float price;
    private String type;
    private String templateId;

    public RequestSend(String userId, String product, String unitType, Float price, String type, String templateId) {
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

    public String getUnitType() {
        return unitType;
    }

    public Float getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getTemplateId() {
        return templateId;
    }
}
