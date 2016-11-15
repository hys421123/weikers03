package com.team.witkers.activity.setting;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.utils.MyToast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/8/5.
 */
public class ChangePwdActivity extends BaseActivity {
    private Toolbar toolbar;
    private EditText et_currentPwd,et_newPwd,et_verifyPwd;
    @Override
    protected int setContentId() {
        return R.layout.activity_setting_changepwd;
    }

    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        et_currentPwd= (EditText) findViewById(R.id.et_currentPwd);
        et_newPwd= (EditText) findViewById(R.id.et_newPwd);
        et_verifyPwd= (EditText) findViewById(R.id.et_verifyPwd);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("修改登录密码");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        if(item.getItemId()==R.id.changepwd_done){
            changePwd();
//            MyToast.showToast(this,"修改完成");
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_changepwd, menu);
        return true;
    }

    private void changePwd(){
        String currentPwd=et_currentPwd.getText().toString();
        String newPwd=et_newPwd.getText().toString();
        String verifyPwd=et_verifyPwd.getText().toString();

        if(!currentPwd.isEmpty()&&!newPwd.isEmpty()&&!verifyPwd.isEmpty()){

            if(!newPwd.equals(verifyPwd)){
                MyToast.showToast(ChangePwdActivity.this,"确认密码不一致");
                return;
            }
            MyApplication.mUser.updateCurrentUserPassword(currentPwd, newPwd, new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        MyToast.showToast(ChangePwdActivity.this,"密码修改成功，可以用新密码进行登录啦");
                        finish();
                    }else{
                        MyToast.showToast(ChangePwdActivity.this,"密码修改失败:" + e.getMessage());
                    }
                }

            });
        }else{
            MyToast.showToast(this,"亲，请填写完整");
        }


    }
}//ChangePwd_cls
