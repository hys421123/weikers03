package com.team.witkers.bean;

import java.io.Serializable;

/**
 * Created by hys on 2016/9/8.
 */
//包括认领金额和认领用户名
public class ClaimItems implements Serializable {
    private String claimName;
    private float claimMoney;
    private String claimHeadUrl;
    private String claimTime;
    //claimFlag认领标志位，分为认领、已被某人认领、任务认领完成
    //分别用0,1,2表示三种 状态
    private int claimFlag=0;

    public ClaimItems(String claimName, float claimMoney,String claimHeadUrl,String claimTime,int claimFlag){
        this.claimName=claimName;
        this.claimMoney=claimMoney;
        this.claimHeadUrl = claimHeadUrl;
        this.claimTime = claimTime;
        this.claimFlag = claimFlag;
    }

    public int getClaimFlag() {
        return claimFlag;
    }

    public void setClaimFlag(int claimFlag) {
        this.claimFlag = claimFlag;
    }

    public String getClaimHeadUrl() {
        return claimHeadUrl;
    }

    public void setClaimHeadUrl(String claimHeadUrl) {
        this.claimHeadUrl = claimHeadUrl;
    }

    public String getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(String claimTime) {
        this.claimTime = claimTime;
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




}
