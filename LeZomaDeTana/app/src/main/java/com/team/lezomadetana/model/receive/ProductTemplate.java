package com.team.lezomadetana.model.receive;

import java.util.List;

/**
 * Created by Hery Andoniana on 08/09/2018.
 **/

public class ProductTemplate {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private String id;
    private String name;
    private List<String> assetUrls;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProductTemplate() {
    }

    public ProductTemplate(String id, String name, List<String> assetUrls) {
        this.id = id;
        this.name = name;
        this.assetUrls = assetUrls;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssetUrls(List<String> assetUrls) {
        this.assetUrls = assetUrls;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getAssetUrls() {
        return assetUrls;
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

    // ===========================================================
}



