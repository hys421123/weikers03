package com.team.witkers.activity.orders;

import android.os.Bundle;

import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;

/**
 * Created by lenovo on 2016/12/5.
 */

public class WeikersMsgActivity extends BGAPPToolbarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_weikers_notify);
    }

    @Override
    protected void setListener() {
        setTitle("微客消息");
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}//WeikersMsgAct_cls
