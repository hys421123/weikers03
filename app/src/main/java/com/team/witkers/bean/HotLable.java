package com.team.witkers.bean;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by jin on 2016/10/6.
 */
public class HotLable extends BmobObject implements Serializable{

    private List<String> hotLableList;

    public HotLable(List<String> hotLableList){
        this.hotLableList = hotLableList;
    }

    public List<String> getHotLableList() {
        return hotLableList;
    }

    public void setHotLableList(List<String> hotLableList) {
        this.hotLableList = hotLableList;
    }
}
