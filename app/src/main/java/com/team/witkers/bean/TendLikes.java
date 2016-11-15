package com.team.witkers.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by hys on 2016/8/10.
 */
public class TendLikes extends BmobObject{

    private String likeUserName;
    private TendItems tendItems;//评论的动态项

    public TendItems getTendItems() {
        return tendItems;
    }

    public void setTendItems(TendItems tendItems) {
        this.tendItems = tendItems;
    }

    public String getLikeUserName() {
        return likeUserName;
    }

    public void setLikeUserName(String likeUserName) {
        this.likeUserName = likeUserName;
    }


}
