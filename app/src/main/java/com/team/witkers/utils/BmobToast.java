package com.team.witkers.utils;

import android.content.Context;

import com.hys.mylog.MyLog;

/**
 * Created by hys on 2016/8/11.
 */
public class BmobToast {
    public static void failureShow(Context context,String toast,String eMsg){
        MyToast.showToast(context,toast+"_"+eMsg);
        MyLog.e(eMsg);
    }

    public static void successShow(Context context,String toast){
        MyToast.showToast(context,toast);
        MyLog.i(toast);
    }
}
