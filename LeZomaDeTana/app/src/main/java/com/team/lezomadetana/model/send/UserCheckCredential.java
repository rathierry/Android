package com.team.lezomadetana.model.send;

/**
 * Created by Hery Andoniaina on 03/09/2018.
 */

public class UserCheckCredential {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String username;
    private String password;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UserCheckCredential() {
    }

    public UserCheckCredential(String username, String password) {
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
