package com.team.witkers.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by hys on 2016/7/29.
 */
public class MyUser extends BmobUser implements Serializable{
    //其他属性BmobUser都已经具有不用填写
    // 包括username、password、mobilePhoneNumber、mobilePhoneNumberVerified、
    private String headUrl="";//头像地址
//    private BmobRelation likeTendItems; //所点赞的动态，希望由user得到相应点赞过的动态项
    private BmobRelation takeMissions;//认领的所有任务
    private BmobRelation concernPerson;//关注的人
    private BmobRelation fansPerson;//他的粉丝们
    private String nickName="";
    private String relName="";
    private String sex="";
    private String location="";
    private String Introduce="";

    public BmobRelation getFansPerson() {
        return fansPerson;
    }

    public void setFansPerson(BmobRelation fansPerson) {
        this.fansPerson = fansPerson;
    }

    public BmobRelation getConcernPerson() {
        return concernPerson;
    }

    public void setConcernPerson(BmobRelation concernPerson) {
        this.concernPerson = concernPerson;
    }

    public String getNickName() {
        return nickName;
    }

    public BmobRelation getTakeMissions() {
        return takeMissions;
    }

    public void setTakeMissions(BmobRelation takeMissions) {
        this.takeMissions = takeMissions;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntroduce() {
        return Introduce;
    }

    public void setIntroduce(String introduce) {
        Introduce = introduce;
    }

//    public BmobRelation getLikeTendItems() {
//        return likeTendItems;
//    }
//
//    public void setLikeTendItems(BmobRelation likeTendItems) {
//        this.likeTendItems = likeTendItems;
//    }

    public String getHeadUrl() {
        return headUrl;
    }
    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }


}
