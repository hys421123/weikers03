package com.team.witkers.bean;

import java.io.Serializable;

/**
 * Created by hys on 2016/10/25.
 */

public class ChooseClaimant implements Serializable {
    //选定认领人，  只有一个
    //只有一名认领人， 包括认领姓名，认领金额，认领状态（任务进行还是任务已完成）
    private String claimName;
    private float claimMoney;
    private boolean claimStatus=false;//认领状态
    //false为任务进行中，true为任务已完成

    //认领人头像
    private String claimHeadUrl;

    public void setClaimHeadUrl(String claimHeadUrl) {
        this.claimHeadUrl = claimHeadUrl;
    }

    public String getClaimHeadUrl() {
        return claimHeadUrl;
    }

    public String getClaimName() {
        return claimName;
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public float getClaimMoney() {
        return claimMoney;
    }

    public void setClaimMoney(float claimMoney) {
        this.claimMoney = claimMoney;
    }

    public boolean isClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(boolean claimStatus) {
        this.claimStatus = claimStatus;
    }



}
