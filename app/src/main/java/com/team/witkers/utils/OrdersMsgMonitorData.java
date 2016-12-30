package com.team.witkers.utils;

import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.eventbus.ChooseNotify;
import com.team.witkers.eventbus.OrdersMsgEvent2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by hys on 2016/8/26.
 */
public class OrdersMsgMonitorData {
    //监听 订单消息的

    private static BmobRealTimeData bmobRealTimeData;

    public static void monitorData(){
         bmobRealTimeData= new BmobRealTimeData();
        bmobRealTimeData.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
//                MyLog.i( "订单监听连接成功:"+ bmobRealTimeData.isConnected());
                if( bmobRealTimeData.isConnected()){
                    // 监听表更新
                    bmobRealTimeData.subTableUpdate("Mission");
                }
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
//                MyLog.v(jsonObject.optString("action"));
//                MyLog.v("判断是否主线程_ "+(Thread.currentThread() == Looper.getMainLooper().getThread()) );
                //  判断是主线程啊！！
//                MyLog.i( "数据："+jsonObject);
                jsonParse(jsonObject);
            }
        });
    }//OrdersMsgMonitorData

    private static void jsonParse(JSONObject jsonObject){
        try {
            MyLog.i("Event Bus 执行了");
            JSONObject jsonData = jsonObject.getJSONObject("data");
//            final Mission mission= JSON.parseObject(jsonData.toString(), Mission.class);
//            EventBus.getDefault().post(new OrdersMsgEvent(mission));
            MyLog.i(jsonData.toString());
            String pubUserName = jsonData.getString("pubUserName");
            int times = jsonData.getInt("times");
            if (times == 0)//为0 表示新发布的任务
                EventBus.getDefault().post(new OrdersMsgEvent2(jsonData));
            else {  //若不是新发布的任务

                JSONObject jsonObj1 = jsonData.getJSONObject("chooseClaimant");
                String claimName = jsonObj1.getString("claimName");

                MyLog.v("claimName_ " + claimName);
                if (MyApplication.mUser.getUsername().equals(claimName)) {
//                    MyLog.v("你被" + pubUserName + "选为接单者，赶紧去完成任务吧!");
                    //通知 显示小红点
                    EventBus.getDefault().post(new ChooseNotify());
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            MyLog.e("json解析失败"+e.getMessage());
        }


    }//jsonParse



    public static void stopMonitor(){
        bmobRealTimeData.unsubTableUpdate("MissionTaker");
    }//stop

/*    private static void setNum(String myMissionId,final JSONObject jsonData){

        BmobQuery<Mission> bmobQuery = new BmobQuery<Mission>();
        bmobQuery.getObject(myMissionId, new QueryListener<Mission>() {
            @Override
            public void done(Mission mission, BmobException e) {
                int NumTemp = mission.getMissionTakerNum();
                MyLog.i("NumTemp-->"+NumTemp);
                EventBus.getDefault().post(new OrdersMsgEvent(jsonData,NumTemp));
                String infoStr = jsonData.optString("info");
                int num = NumTemp;
                String time = jsonData.optString("createdAt");
                String takerUserHeadUrl = jsonData.optString("takerUserHeadUrl");
                String pubUserHeadUrl = jsonData.optString("pubUserHeadUrl");
                String takerName = jsonData.optString("takerName");
                String pubUserName = jsonData.optString("pubUserName");
                pubOrdersMsg(infoStr,num,time,takerUserHeadUrl,pubUserHeadUrl,takerName,pubUserName);
            }
        });
    }

    *//**
     *
     * @param infoStr
     * @param num
     * @param time
     * @param takerUserHeadUrl
     * @param pubUserHeadUrl
     *//*
    private static void pubOrdersMsg(String infoStr,int num,String time,String takerUserHeadUrl,
                                     String pubUserHeadUrl,String takerName,String pubUserName){
        OrdersMsg ordersMsg = new OrdersMsg();
        ordersMsg.setInfo(infoStr);
        ordersMsg.setMissionTakerNum(num);
        ordersMsg.setTime(time);
        ordersMsg.setTakerUserHeadUrl(takerUserHeadUrl);
        ordersMsg.setPubUserHeadUrl(pubUserHeadUrl);
        ordersMsg.setTakerName(takerName);
        ordersMsg.setPubUserName(pubUserName);

        ordersMsg.save(new SaveListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e==null){
                    MyLog.i("成功上传Msg了---");
                }
            }
        });
    }*/
}
