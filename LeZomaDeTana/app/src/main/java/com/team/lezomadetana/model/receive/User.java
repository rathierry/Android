package com.team.lezomadetana.model.receive;

/**
 * Created by RaThierry on 11/09/2018.
 **/

public class User {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String id;
    private String creationTime;
    private String updateTime;
    private String username;
    private String name;
    private String region;
    private String profileImageUrl;
    private String password;

    // ===========================================================
    // Constructors
    // ===========================================================

    public User() {
    }

    public User(String id, String creationTime, String updateTime, String username, String name, String region, String profileImageUrl) {
        this.id = id;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.username = username;
        this.name = name;
        this.region = region;
        this.profileImageUrl = profileImageUrl;
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

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
