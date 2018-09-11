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
    private User user;
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

    public Request(Boolean active, ArrayList<Offer> offers, User user, ArrayList<String> assetUrls, UnitType unitType, Float price, String userId, String product, String templateId, Integer quantity, String id, Type type, String userName) {
        this.active = active;
        this.offers = offers;
        this.user = user;
        this.assetUrls = assetUrls;
        this.unitType = unitType;
        this.price = price;
        this.userId = userId;
        this.product = product;
        this.templateId = templateId;
        this.quantity = quantity;
        this.id = id;
        this.type = type;
        this.userName = userName;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<String> getAssetUrls() {
        return assetUrls;
    }

    public void setAssetUrls(ArrayList<String> assetUrls) {
        this.assetUrls = assetUrls;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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
