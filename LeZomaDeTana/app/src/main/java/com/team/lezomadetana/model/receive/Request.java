package com.team.lezomadetana.model.receive;

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

    private Boolean active;
    private ArrayList<Offer> offers;
    private ArrayList<String> assetUrls;
    private UnitType unitType;
    private Float price;
    private String userId;
    private String product;
    private String templateId;
    private Integer quantity;
    private String id;
    private Type type;
    private String userName;

    private boolean isImportant;
    private boolean isRead;
    private int color = -1;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Request() {
    }

    public Request(Boolean active, ArrayList<String> assetUrls, String userId, String userName, UnitType unitType, ArrayList<Offer> offers, Float price, Integer quantity, String product, String templateId, String id, Type type) {
        this.active = active;
        this.assetUrls = assetUrls;
        this.userId = userId;
        this.userName = userName;
        this.unitType = unitType;
        this.offers = offers;
        this.price = price;
        this.quantity = quantity;
        this.product = product;
        this.templateId = templateId;
        this.id = id;
        this.type = type;
    }

    // ===========================================================
    // Getter & Setter
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

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setType(Type type) {
        this.type = type;
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

    public String getUserName() {
        return userName;
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

    public Type getType() {
        return type;
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

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================
}
