package com.team.witkers.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * mission:任务的信息
 * Created by zcf on 2016/4/15.
 */
public class Mission extends BmobObject implements Serializable {
    private MyUser pubUser;//任务发布者
    private BmobRelation missionTaker;//任务认领者
//    private List<Float> claimMoney;//任务认领金额，跟任务认领者顺序保持一致
//    private List<String> claimName;//认领人用户名，不能重复的,跟前面的项保持一致
    private List<ClaimItems> claimItemList;//认领项，包括认领人用户名和认领金额，不能重复
    private ChooseClaimant chooseClaimant;//选定认领人
    private String claimName=""; //选定的认领人用户名  默认空字符串
    private boolean isFinished=false;//任务完成状态,默认是未完成

    private String finishTime="0";//任务完成时间
    private float charges;//佣金
    private String pubUserName;
    private String pubUserHeadUrl;

    private String name;//
    private String phone;//电话
    private String address;//地址

    private String info;//需求
    private Integer capital;//本金
    private Date dealTime;//完成交易时间
    private String category;//类别 标签



    private double latitude;
    private double longitude;

    private BmobGeoPoint missionLocation;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public String getClaimName() {
        return claimName;
    }

    public List<ClaimItems> getClaimItemList() {
        return claimItemList;
    }

    public void setClaimItemList(List<ClaimItems> claimItemList) {
        this.claimItemList = claimItemList;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
    public boolean getFinished(){
        return  isFinished;
    }

    public void setChooseClaimant(ChooseClaimant chooseClaimant) {
        this.chooseClaimant = chooseClaimant;
    }

    public ChooseClaimant getChooseClaimant() {
        return chooseClaimant;
    }

    public BmobGeoPoint getMissionLocation() {
        return missionLocation;
    }

    public void setMissionLocation(BmobGeoPoint missionLocation) {
        this.missionLocation = missionLocation;
    }
//    public void setClaimName(List<String> claimName) {
//        this.claimName = claimName;
//    }
//
//    public List<String> getClaimName() {
//        return claimName;
//    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMissionTaker(BmobRelation missionTaker) {
        this.missionTaker = missionTaker;
    }

    public BmobRelation getMissionTaker() {
        return missionTaker;
    }

//    public void setClaimMoney(List<Float> claimMoney) {
//        this.claimMoney = claimMoney;
//    }
//
//    public List<Float> getClaimMoney() {
//        return claimMoney;
//    }

    public String getPubUserHeadUrl() {
        return pubUserHeadUrl;
    }

    public void setPubUserHeadUrl(String pubUserHeadUrl) {
        this.pubUserHeadUrl = pubUserHeadUrl;
    }
    public String getPubUserName() {
        return pubUserName;
    }

    public void setPubUserName(String pubUserName) {
        this.pubUserName = pubUserName;
    }

    public float getCharges() {
        return charges;
    }
    public void setCharges(float charges) {
        this.charges = charges;
    }
    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getFinishTime() {

        return finishTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {

        return address;
    }



    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {

        return phone;
    }

    public void setPubUser(MyUser pubUser) {
        this.pubUser = pubUser;
    }


    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCapital(Integer capital) {
        this.capital = capital;
    }



    public void setCategory(String category) {
        this.category = category;
    }

    public MyUser getPubUser() {

        return pubUser;
    }


    public Date getDealTime() {
        return dealTime;
    }

    public String getInfo() {
        return info;
    }

    public Integer getCapital() {
        return capital;
    }



    public String getCategory() {
        return category;
    }
}
