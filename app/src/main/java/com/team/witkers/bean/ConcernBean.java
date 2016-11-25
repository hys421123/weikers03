package com.team.witkers.bean;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by jin on 2016/10/8.
 */
public class ConcernBean extends BmobObject implements Serializable {
//在用户注册时，对应每个 用户添加一个concernBean, 用来保存 关注粉丝等信息


    private String name;
    private BmobRelation fans;
    private BmobRelation concerns;


    private List<ConcernFans> concernsList;//关注者栏
    private List<ConcernFans> fansList;//粉丝栏

    public void setFansList(List<ConcernFans> fansList) {
        this.fansList = fansList;
    }

    public List<ConcernFans> getFansList() {
        return fansList;
    }

    public void setConcernsList(List<ConcernFans> concernsList) {
        this.concernsList = concernsList;
    }

    public List<ConcernFans> getConcernsList() {
        return concernsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobRelation getFans() {
        return fans;
    }

    public void setFans(BmobRelation fans) {
        this.fans = fans;
    }

    public BmobRelation getConcerns() {
        return concerns;
    }

    public void setConcerns(BmobRelation concerns) {
        this.concerns = concerns;
    }
}
