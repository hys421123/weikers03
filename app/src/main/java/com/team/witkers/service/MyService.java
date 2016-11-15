package com.team.witkers.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.bean.TendComments;
import com.team.witkers.eventbus.MsgEvent;
import com.team.witkers.utils.MonitorData;
import com.team.witkers.utils.OrdersMsgMonitorData;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by hys on 2016/8/29.
 */
public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.v("开启服务service");
        //在第一次开启服务的时候装载动态消息
//        loadMsgTendFirst();

        //主要是应对重新登录 加载的消息
        //在这里读取 user 对应的消息记录吧
//        MyLog.v("and there???");
        BmobQuery<TendComments> query3=new BmobQuery<>();
        query3.addWhereEqualTo("tendUser", MyApplication.mUser);
        query3.order("-createdAt");
        query3.setLimit(8);
        query3.include("tendItems");
        String userName="";
        if( MyApplication.mUser!=null)
            userName=MyApplication.mUser.getUsername();
        query3.addWhereNotEqualTo("commentUserName",userName);

        query3.findObjects(new FindListener<TendComments>() {
            @Override
            public void done(List<TendComments> list, BmobException e) {
                if(e==null){
                    EventBus.getDefault().post(new MsgEvent(list));
//                    MyLog.i("消息查询成功333");
                }else{
                    MyLog.e("消息查询失败333"+e.getMessage());
                    }//else
            }//done
        });

        //开启监听数据，包括对订单和动态的监听
        MonitorData.monitorData();
        OrdersMsgMonitorData.monitorData();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        MyLog.v("onStartCOmmand_ service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MyLog.e("结束Service");
        EventBus.getDefault().post(new MsgEvent(true));
        OrdersMsgMonitorData.stopMonitor();
        MonitorData.stopMonitor();
        super.onDestroy();
    }

    private class  MyBinder extends Binder implements MyBinderInterface{

        @Override
        public void showMsg() {

        }
    }//MyBinder_cls

}
