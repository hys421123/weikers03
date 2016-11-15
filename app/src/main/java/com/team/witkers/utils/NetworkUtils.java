package com.team.witkers.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by hys on 2016/8/25.
 */
public class NetworkUtils {

    public static boolean isNetWorkConnet(Context context){
        ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi|internet){
            //执行相关操作
            return true;
        }else{
            Toast.makeText(context, "亲，网络连了么？", Toast.LENGTH_LONG)
                    .show();
            return false;
        }//else
    }//isNetWorkConnect
}
