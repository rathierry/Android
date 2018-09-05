package com.team.lezomadetana.model.receive;

import java.util.List;

public class Request
{
    private Boolean active;
    private List<String> assetUrls;
    private String userId;
    private String unitType;
    private List<String> offers;
    private Float price;
    private Integer quantity;

    private String product;
    private String templateId;
    private String id;
    private Integer type;

    public Request() {
    }

    public Request(Boolean active, List<String> assetUrls, String userId, String unitType, List<String> offers, Float price, Integer quantity, String product, String templateId, Integer type) {
        this.active = active;
        this.assetUrls = assetUrls;
        this.userId = userId;
        this.unitType = unitType;
        this.offers = offers;
        this.price = price;
        this.quantity = quantity;
        this.product = product;
        this.templateId = templateId;
        this.type = type;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAssetUrls(List<String> assetUrls) {
        this.assetUrls = assetUrls;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public void setOffers(List<String> offers) {
        this.offers = offers;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getActive() {

        return active;
    }

    public List<String> getAssetUrls() {
        return assetUrls;
    }

    public String getUserId() {
        return userId;
    }

    public String getUnitType() {
        return unitType;
    }

    public List<String> getOffers() {
        return offers;
    }

    public Float getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getProduct() {
        return product;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }
}
