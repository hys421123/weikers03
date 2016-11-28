package com.team.witkers.activity.pubclaim;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
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
 * Created by hys on 2016/11/9.
 */

public class AllOrdersActivity extends BaseActivity {
//全部订单界面

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MissionStateAdapter mAdapter;
    private List<Mission> dataList = new ArrayList<>();
    private int LIMIT = 8;        // 每页的数据是8条
    @Override
    protected int setContentId() {
        return R.layout.activity_all_orders_2;
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("全部订单");
        setSupportActionBar(toolbar);
    }
    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView= (RecyclerView)findViewById(R.id.mRecyclerView);


        linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
                        MyToast.showToast(AllOrdersActivity.this,"没有数据,快去发布吧!");
                        return;
                    }

                    for (Mission mission: list) {
                            dataList.add(mission);
                    }
                    mAdapter = new MissionStateAdapter(AllOrdersActivity.this,dataList);
                    mRecyclerView.setAdapter(mAdapter );

                }else{
                    MyLog.e("查询mission失败"+e.getMessage());
                }//else
            }
        });

    }//initData


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}//AllOrdersAct_cls
