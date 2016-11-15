package com.team.witkers.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by jin on 2016/10/8.
 */
public class ConcernBean extends BmobObject implements Serializable {

    private String name;
    private MyUser fans;
    private MyUser concerns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyUser getFans() {
        return fans;
    }

    public void setFans(MyUser fans) {
        this.fans = fans;
    }

    public MyUser getConcerns() {
        return concerns;
    }

    public void setConcerns(MyUser concerns) {
        this.concerns = concerns;
    }
}
