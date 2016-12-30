package com.team.witkers.eventbus;

import org.json.JSONObject;

/**
 * Created by jin on 2016/9/12.
 */
public class OrdersMsgEvent2 {
// 订单消息发生变化时， 此对象监听产生
    private JSONObject jsonObject;

    public OrdersMsgEvent2(JSONObject jsonObject){

        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonData() {
        return jsonObject;
    }

}
