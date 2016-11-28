package com.team.witkers.activity.pubclaim;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.adapter.MissionStateAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.Mission;
import com.team.witkers.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by hys on 2016/11/4.
 */

public class MyClaimActivity extends BaseActivity implements View.OnClickListener {
//  我的认领界面
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout ll_allOrders;

    private int LIMIT = 8;        // 每页的数据是8条
    private List<Mission> dataList = new ArrayList<>();
    private MissionStateAdapter mAdapter;

    @Override
    protected int setContentId() {
        return R.layout.activity_myclaim_2;
    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle("我的认领");
        setSupportActionBar(toolbar);
    }
    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView= (RecyclerView)findViewById(R.id.mRecyclerView);
        ll_allOrders= (LinearLayout) findViewById(R.id.ll_allOrders);

        linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void setListener() {
        ll_allOrders.setOnClickListener(this);
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
    protected void initData() {
        BmobQuery<Mission> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        //返回8条数据，
        query.setLimit(LIMIT);
        boolean isCache = query.hasCachedResult(Mission.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }

        query.addWhereEqualTo("missionTaker", MyApplication.mUser);
        
        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询mission成功 size_"+list.size());
                    if (list.size() == 0) {
                        MyToast.showToast(MyClaimActivity.this,"没有数据,快去发布吧!");
//                        if (mDialog != null) {
//                            mDialog.dismiss();
//                        }
                        return;
                    }

                    for (Mission mission: list) {
                        //若该任务确定的认领人正是这个missionTaker的话，说明该任务待完成、正在进行中
                        if(mission.getChooseClaimant()!=null&&mission.getChooseClaimant().getClaimName().equals(MyApplication.mUser.getUsername()))
                        {
                            dataList.add(mission);
                        }

//                        MyLog.i("pubUsername_ "+lb.getPubUser().getUsername());

                    }
                    mAdapter = new MissionStateAdapter(MyClaimActivity.this,dataList);
                    mRecyclerView.setAdapter(mAdapter );

                }else{
                    MyLog.e("查询mission失败"+e.getMessage());
                    }//else
            }
        });

    }//initData

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_allOrders:
                MyLog.v("all orders");
                startActivity(new Intent(MyClaimActivity.this,AllOrdersActivity.class));


                break;
        }//switch
    }//onClick
}//MyClaimAct_cls
