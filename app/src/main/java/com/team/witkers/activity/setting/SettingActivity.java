package com.team.witkers.activity.setting;


import com.gc.materialdesign.widgets.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.LoginActivity;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendItems;
import com.team.witkers.service.MyService;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/8/4.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private LinearLayout ll_nobother,ll_acountsafe,ll_about,ll_logout;
    private Button btn_clearAllLikes;
    private ProgressDialog mDialog;
   private List<BmobObject> likeList;//存储所有为true的点赞条目
    @Override
    protected int setContentId() {
        return R.layout.activity_setting_2;
    }

    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        
        ll_nobother= (LinearLayout) findViewById(R.id.ll_nobother);
        ll_acountsafe= (LinearLayout) findViewById(R.id.ll_acountsafe);
        ll_about= (LinearLayout) findViewById(R.id.ll_about);
        ll_logout= (LinearLayout) findViewById(R.id.ll_logout);
        btn_clearAllLikes= (Button) findViewById(R.id.btn_clearAllLikes);

    }

    @Override
    protected void setListener() {
        ll_nobother.setOnClickListener(this);
        ll_acountsafe.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        btn_clearAllLikes.setOnClickListener(this);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("设置");
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
            case R.id.ll_nobother:

                break;
            case R.id.ll_acountsafe:
                startActivity(new Intent(this,AccountSafeActivity.class));
                break;
            case R.id.ll_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            case R.id.ll_logout:
//                MyLog.i("logout linearlayout");
                mDialog = new ProgressDialog(this,"正在退出...");
                mDialog.show();
                BmobUser.logOut(); //清除缓存用户对象,退出登录
                MyApplication.mUser=null;
                //结束服务
                stopService(new Intent(this, MyService.class));
                MyApplication.SETLOGOUT=true;
//                mDialog.dismiss();
                //把所有已经点赞的项目isLike 清空1
                queryLikes();
            case R.id.btn_clearAllLikes:
//                updateAllLikes();
                break;
        }//switch
    }//onClick

    private void clearAllLikes(){//直接删除isLike字段

    }//clearAllLikes

    private void queryLikes(){//先要查出所点出的赞条目，在把这些条目置初始值
        likeList=new ArrayList<>();
        BmobQuery<TendItems> queryLikes=new BmobQuery<>();
        queryLikes.addWhereEqualTo("isLike",true);

        queryLikes.findObjects(new FindListener<TendItems>() {
            @Override
            public void done(List<TendItems> list, BmobException e) {
                if (e == null) {
                    MyLog.i("isLike_true查询成功");

                    if (list.size()!=0) {
                        for (int i = 0; i < list.size(); i++) {
                            TendItems tendNew = list.get(i);
                            tendNew.setLike(false);
                            likeList.add(tendNew);
                        }
//                    MyLog.v("updateAllLikes之前");
                        updateAllLikes();
                    }else {//if
                        Intent intent3 = new Intent(SettingActivity.this, LoginActivity.class);

                        startActivity(intent3);
                        finish();
                        if (mDialog != null)
                            mDialog.dismiss();
                    }


                }
                else{
                      MyLog.e("isLike_true查询失败");
                          if(mDialog!=null)
                              mDialog.dismiss();
                    }
            }//done
        });//findObjs
    }//queryLikes

    private void updateAllLikes(){
        if(likeList==null||likeList.size()==0) {
            MyLog.i("likeList null");

            return;
        }
        else {
            new BmobBatch().updateBatch(likeList).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            BatchResult result = list.get(i);
                            BmobException ex = result.getError();
                            if (ex != null) {
                                MyLog.e("第" + i + "个数据批量更新失败：" + ex.getMessage() + "," + ex.getErrorCode());
                            }
                        }
                        MyLog.i("likeItems更改成功");
                    Intent intent3 = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent3);
                    finish();
                        if (mDialog != null)
                            mDialog.dismiss();
                    } else {
                        MyLog.e("失败：" + e.getMessage() + "," + e.getErrorCode());
                        if (mDialog != null)
                            mDialog.dismiss();
                    }
                }//done
            });//updateBatch



        }//else
        if (mDialog != null)
            mDialog.dismiss();
    }//updateAllLikes
}//SettingAct
