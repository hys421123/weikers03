package com.team.witkers.activity.setting;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;

/**
 * Created by hys on 2016/8/4.
 */
public class AboutActivity extends BaseActivity {
    private Toolbar toolbar;

    @Override
    protected int setContentId() {
        return R.layout.activity_setting_about_2;
    }


    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);

    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
