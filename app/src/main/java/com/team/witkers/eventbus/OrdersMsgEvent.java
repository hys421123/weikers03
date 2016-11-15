package com.team.witkers.eventbus;

import com.team.witkers.bean.Mission;

/**
 * Created by jin on 2016/9/2.
 */
public class OrdersMsgEvent {
    private Mission mission;

    public OrdersMsgEvent(Mission mission){

        this.mission = mission;
    }

    public Mission getJsonData() {
        return mission;
    }





}
