package com.team.lezomadetana.model.receive;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Hery Andoniaina on 06/09/2018.
 */

public class Request {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @Expose
    private Boolean active;
    @Expose
    private ArrayList<String> assetUrls;
    @Expose
    private String userId;
    private UnitType unitType;
    @Expose
    private ArrayList<Offer> offers;
    @Expose
    private Float price;
    private Integer quantity;

    private String product;
    private String templateId;
    private String id;
    private Integer type;
    private String picture;

    private boolean isImportant;
    private boolean isRead;
    private int color = -1;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Request() {
    }

    public Request(Boolean active, ArrayList<String> assetUrls, String userId, UnitType unitType, ArrayList<Offer> offers, Float price, Integer quantity, String product, String templateId,Integer type) {
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

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAssetUrls(ArrayList<String> assetUrls) {
        this.assetUrls = assetUrls;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public void setOffers(ArrayList<Offer> offers) {
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

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public Boolean getActive() {
        return active;
    }

    public ArrayList<String> getAssetUrls() {
        return assetUrls;
    }

    public String getUserId() {
        return userId;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public ArrayList<Offer> getOffers() {
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

    public String getPicture() {
        return picture;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getColor() {
        return color;
    }


    public enum UnitType {KG, M, L, M2, UNIT}

    public enum Type {SELL, BUY}
}
