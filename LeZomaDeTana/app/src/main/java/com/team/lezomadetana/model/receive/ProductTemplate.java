package com.team.lezomadetana.model.receive;

import java.util.List;

public class ProductTemplate
{

    private String id;
    private String name;

    private List<String> assetUrls;

    public ProductTemplate() {
    }

    public ProductTemplate(String name, List<String> assetUrls) {
        this.name = name;
        this.assetUrls = assetUrls;
    }

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
}
