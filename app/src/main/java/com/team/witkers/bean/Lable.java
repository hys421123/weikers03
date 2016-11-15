package com.team.witkers.bean;

import java.io.Serializable;

/**
 * Created by jin on 2016/9/26.
 */
public class Lable implements Serializable {

    private String headUrl;
    private String lableName;

    public Lable(String headUrl, String lableName){
        this.headUrl = headUrl;
        this.lableName = lableName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }
}
