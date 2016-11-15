package com.team.witkers.utils;

import com.alibaba.fastjson.JSON;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendComments;
import com.team.witkers.bean.TendItems;
import com.team.witkers.eventbus.MonitorEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by hys on 2016/8/26.
 */
public class MonitorData {
    private static BmobRealTimeData    bmobRealTimeData;
    public static void monitorData(){
         bmobRealTimeData= new BmobRealTimeData();
        bmobRealTimeData.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
//                MyLog.i( "动态监听连接成功:"+ bmobRealTimeData.isConnected());
                if( bmobRealTimeData.isConnected()){
                    // 监听动态项表更新
                    bmobRealTimeData.subTableUpdate("TendItems");
//                    bmobRealTimeData.subTableUpdate("Mission");
                }
            }

            @Override
            public void onDataChange(JSONObject data) {
//                MyLog.v(data.optString("action"));
//                MyLog.v("判断是否主线程_ "+(Thread.currentThread() == Looper.getMainLooper().getThread()) );
                //  判断是主线程啊！！
//                MyLog.i( "数据："+data);

                jsonParse(data);
            }
        });
    }//MonitorData

    private static void jsonParse(JSONObject jsonObj){
        try {

//            JSON.parseObject(jsonObj, TendItems.class);
            JSONObject jsonData= jsonObj.getJSONObject("data");
            String friendName=jsonData.getString("friendName");
//            MyLog.i("TendItems_Name_ "+friendName);
            //若监听的好友不是当前用户，不予返回监听内容
            //即  只监听当前登录用户的更新状态
            if(!friendName.equals(BmobUser.getCurrentUser(MyUser.class).getUsername())){
                return;
            }
            //查询相应的tendComments 最近更新的
            if(MyApplication.mUser==null) {
                MyLog.e("当前用户不存在");
                return;
            }
           int commentNum=jsonData.getInt("commentNum");
            if(commentNum==0){
                MyLog.v("comment_0 表示发新状态，而不是评论，不予监听");
                return;
            }
           String createTime= jsonData.getString("createdAt");
//            MyLog.v("create_ "+createTime);
            final TendItems tend=JSON.parseObject(jsonData.toString(), TendItems.class);
            MyLog.v("经过fastJson");
//            MyLog.i("11MonitorData_tendTime_ "+tend.getCreatedAt());
            tend.setCreateTime(createTime);

            BmobQuery<TendComments> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("tendItems",tend);
            query2.order("-createdAt");
            query2.setLimit(1);
            query2.include("tendItems");
            query2.findObjects(new FindListener<TendComments>() {
                @Override
                public void done(List<TendComments> list, BmobException e) {
                    if(e==null){
                        MyLog.i("查询监听评论项成功");
                        //取出最新的评论
                        TendComments comment=list.get(0);
                        if(comment.getCommentUserName().equals(MyApplication.mUser.getUsername()))
                            return;
//                        MyLog.i("commetContent11_ "+comment.getCommentContent());
//                        还要把tendItems也要传过去
                        EventBus.getDefault().post(new MonitorEvent(tend,comment));
//                        for(TendComments comment:list){
//                            MyLog.i("commentContent_ "+comment.getCommentContent());
//                        }
                    }else{
                        MyLog.e("查询监听评论项失败"+e.getMessage());
                        }//else
                }//done
            });//query
//            MyLog.i("tend_ "+tend.getFriendName()+"(( "+tend.getCommentsList().size());
////            String content1=  jsonData.getString("content");

        } catch (JSONException e) {
            e.printStackTrace();
            MyLog.e("json解析失败");
        }
    }//jsonParse

    public static void stopMonitor(){
        bmobRealTimeData.unsubTableUpdate("TendItems");
    }//stop
}
