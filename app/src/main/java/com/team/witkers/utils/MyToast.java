package com.team.witkers.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by hys on 2016/8/1.
 */
public class MyToast {

    private static Toast toast;
    public static void showToast(Context context, String msg) {
        if(toast==null){// many times click for only one show,not a lot of shows
            toast=Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }else
            toast.setText(msg);

        toast.show();

    }//showToast
}
