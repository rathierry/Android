package com.team.lezomadetana.model.receive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hery Andoniaina on 03/09/2018.
 */

public class UserCredentialResponse
{
    @SerializedName("username")
    private String username;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("region")
    @Expose
    private String region;


    public UserCredentialResponse() {
    }

    public UserCredentialResponse(String username, String name, Boolean success, String region) {
        this.username = username;
        this.name = name;
        this.success = success;
        this.region = region;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getRegion() {
        return region;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
