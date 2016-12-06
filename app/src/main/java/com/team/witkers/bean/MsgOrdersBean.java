package com.team.witkers.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hys on 2016/9/9.
 */
//订单消息Bean,从mission中获取出来，不需要上传
public class MsgOrdersBean implements Serializable {
// 针对 接单人的 微客消息通知
    private String pubUserName;
    public void setPubUserName(String pubUserName) {
        this.pubUserName = pubUserName;
    }
    public String getPubUserName() {
        return pubUserName;
    }



    //任务内容
    private String orderContent;

    private int takerNum=0;//认领人数
    private String recentTakerUrl;//最新认领人头像
    private String recentTakeTime;//最新认领人时间
    private List<ClaimItems> claimItemsList;//认领列表信息
    private int claimFlag;//认领标志位
    // 1.有认领人数的，没有确定认领人的；
    // 2.有确定认领人的，任务正在进行中的；
    // 3.有确定认领人的，任务已经完成的；
    private String missionId;

    public MsgOrdersBean(String orderContent, int takerNum, String recentTakerUrl, String recentTakeTime,
                         List<ClaimItems> claimItemsList,String missionId,int textFlag){
        this.orderContent=orderContent;
        this.takerNum=takerNum;
        this.recentTakerUrl=recentTakerUrl;
        this.recentTakeTime=recentTakeTime;
        this.claimItemsList = claimItemsList;
        this.missionId = missionId;
        this.claimFlag = textFlag;
    }

    public MsgOrdersBean(String pubUserName,String orderContent, int takerNum, String recentTakerUrl, String recentTakeTime,
                         List<ClaimItems> claimItemsList,String missionId,int textFlag){
        this.pubUserName=pubUserName;
        this.orderContent=orderContent;
        this.takerNum=takerNum;
        this.recentTakerUrl=recentTakerUrl;
        this.recentTakeTime=recentTakeTime;
        this.claimItemsList = claimItemsList;
        this.missionId = missionId;
        this.claimFlag = textFlag;
    }

    public int getClaimFlag() {
        return claimFlag;
    }

    public void setClaimFlag(int claimFlag) {
        this.claimFlag = claimFlag;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public List<ClaimItems> getClaimItemsList() {
        return claimItemsList;
    }

    public void setClaimItemsList(List<ClaimItems> claimItemsList) {
        this.claimItemsList = claimItemsList;
    }

    public void setRecentTakeTime(String recentTakeTime) {
        this.recentTakeTime = recentTakeTime;
    }

    public void setRecentTakerUrl(String recentTakerUrl) {
        this.recentTakerUrl = recentTakerUrl;
    }

    public String getRecentTakerUrl() {
        return recentTakerUrl;
    }

    public String getRecentTakeTime() {
        return recentTakeTime;
    }

    public int getTakerNum() {
        return takerNum;
    }

    public void setTakerNum(int takerNum) {
        this.takerNum = takerNum;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }




}
