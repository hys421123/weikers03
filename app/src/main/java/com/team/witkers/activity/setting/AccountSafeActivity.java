package com.team.witkers.activity.setting;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;

/**
 * Created by hys on 2016/8/4.
 */
public class AccountSafeActivity  extends BaseActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private RelativeLayout rl_bindphone,rl_bindmail,rl_changepwd;
    private TextView tv_bindNum;

    @Override
    protected int setContentId() {
        return R.layout.activity_setting_acountsafe_2;
    }

    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        rl_bindphone= (RelativeLayout) findViewById(R.id.rl_bindphone);
        rl_bindmail= (RelativeLayout) findViewById(R.id.rl_bindmail);
        rl_changepwd= (RelativeLayout) findViewById(R.id.rl_changpwd);
        tv_bindNum= (TextView) findViewById(R.id.tv_bindNum);
    }

    @Override
    protected void initData() {
        StringBuilder phoneNum=new StringBuilder(MyApplication.mUser.getMobilePhoneNumber());
       phoneNum=phoneNum.replace(3,8,"*****");
      tv_bindNum.setText( phoneNum);
    }

    @Override
    protected void setListener() {
        rl_bindphone.setOnClickListener(this);
        rl_bindmail.setOnClickListener(this);
        rl_changepwd.setOnClickListener(this);

    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("账号安全");
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
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_bindphone:

                break;
            case R.id.rl_bindmail:

                break;
            case R.id.rl_changpwd:
                startActivity(new Intent(this,ChangePwdActivity.class));
                break;
        }//switch
    }//onClick
}//Act_cls
