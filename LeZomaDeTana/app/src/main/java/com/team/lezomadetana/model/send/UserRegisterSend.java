package com.team.lezomadetana.model.send;

/**
 * Created by Hery Andoniaina on 03/09/2018.
 */

public class UserRegisterSend {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String username;
    private String name;
    private String password;
    private String region;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UserRegisterSend() {
    }

    public UserRegisterSend(String username, String name, String password, String region) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.region = region;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRegion() {
        return region;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
