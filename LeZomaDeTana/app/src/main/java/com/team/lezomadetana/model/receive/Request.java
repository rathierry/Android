package com.team.lezomadetana.model.receive;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Request
{

    @Expose
    private Boolean active;
    @Expose
    private List<String> assetUrls;
    @Expose
    private String userId;
    @Expose
    private String unitType;
    @Expose
    private List<String> offers;
    @Expose
    private Float price;
    @Expose
    private Integer quantity;
    @Expose
    private String product;
    @Expose
    private String templateId;
    @Expose
    private String id;
    @Expose
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
