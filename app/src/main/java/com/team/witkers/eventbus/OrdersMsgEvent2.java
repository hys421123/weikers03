package com.team.witkers.eventbus;

import org.json.JSONObject;

/**
 * Created by jin on 2016/9/12.
 */
public class OrdersMsgEvent2 {

    private JSONObject jsonObject;

    public OrdersMsgEvent2(JSONObject jsonObject){

        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonData() {
        return jsonObject;
    }

}
