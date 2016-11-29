package com.team.witkers.bean;

import com.hys.mylog.MyLog;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/11/25.
 */

public class ConcernFans implements Serializable {
    // 保存粉丝或者关注者的 信息

    private String headUrl;
    private String userName;
    private String info;
//    private Boolean isConcerned=false;//是否  互相 关注, 默认 不是 互关


    public ConcernFans(String headUrl, String userName, String info) {
        this.headUrl = headUrl;
        this.userName = userName;
        this.info = info;
//        this.isConcerned = isConcerned;
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

//    public Boolean getConcerned() {
//        return isConcerned;
//    }
//
//    public void setConcerned(Boolean concerned) {
//        isConcerned = concerned;
//    }


    // 重写equals 方法，保证能检查到相同属性的对象
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConcernFans) {
            ConcernFans fans = (ConcernFans) obj;
            if(fans.getHeadUrl().equals(getHeadUrl())&&fans.getUserName().equals(getUserName())
                   ){
                return true;
            }else {//内if
//             MyLog.d(fans.getInfo().equals(getInfo())+"");
//                MyLog.e("false");
                return false;
            }


        }
        return super.equals(obj);
    }
}
