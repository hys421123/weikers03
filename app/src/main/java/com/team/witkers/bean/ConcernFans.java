package com.team.witkers.bean;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/11/25.
 */

public class ConcernFans implements Serializable {
    // 保存粉丝或者关注者的 信息

    private String headUrl;
    private String userName;
    private String info;
    private Boolean isConcerned;//是否关注或成为粉丝



    public ConcernFans(String headUrl, String userName, String info, Boolean isConcerned) {
        this.headUrl = headUrl;
        this.userName = userName;
        this.info = info;
        this.isConcerned = isConcerned;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getConcerned() {
        return isConcerned;
    }

    public void setConcerned(Boolean concerned) {
        isConcerned = concerned;
    }



}
