package com.team.witkers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.concernFans.ConcernsActivity;
import com.team.witkers.adapter.ConcernsFansAdapter;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.bean.ConcernBean;
import com.team.witkers.bean.Mission;
import com.team.witkers.fragment.msgfrm.RecyclerViewAdapter;
import com.team.witkers.utils.NetworkUtils;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by lenovo on 2016/12/20.
 */

public class MissionActivity extends BGAPPToolbarActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener{
    private String title;
    private String userName;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private List<Mission> dataList=new ArrayList<Mission>();
    private TextView tv_no3;
//    private static final int STATE_FIRST = 0;// 第一次载入
//    private static final int STATE_REFRESH = 1;// 下拉刷新
//    private ProgressDialog mDialog;

    private MissionAdapter   mAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        //直接套用 别人现成的xml
        setContentView(R.layout.fragment_message_orders3);
        tv_no3= (TextView) findViewById(R.id.tv_no3);
        mPullLoadMoreRecyclerView= (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView= (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setLinearLayout();
//        mRecyclerViewAdapter = new RecyclerViewAdapter(this);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
//        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        //设置下拉刷新颜色
        mPullLoadMoreRecyclerView.setColorSchemeResources(R.color.swipe_refresh_color);

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        userName=getIntent().getStringExtra("userName1");
        title=getIntent().getStringExtra("title1");
        setTitle(title);

        queryMission();

    }

    @Override
    public void onRefresh() {
        queryMission();
    }

    @Override
    public void onLoadMore() {

    }

    private void queryMission(){
        BmobQuery<Mission> query1=new BmobQuery<>();

        //设置缓存机制，有网络时为网络优先，无网络时缓存优先

        if(NetworkUtils.isNetWorkConnet(this))
            query1.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        else
            query1.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);

        query1.addWhereEqualTo("pubUserName",userName);
        query1.include("pubUser");
        query1.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> list, BmobException e) {

                if(e==null){

                    if(list.size()==0){
                        MyLog.v("任务数为零");
                        tv_no3.setVisibility(View.VISIBLE);
                    }else{
                        dataList=list;

                        if(mAdapter==null){
                            mAdapter=new MissionAdapter(MissionActivity.this,dataList);
                            mPullLoadMoreRecyclerView.setAdapter(mAdapter);
                        }else{
                            mAdapter.notifyDataSetChanged();
                        }//mAdapter not null
                    }

                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();

                }//e==null
                else{//e not null
                    MyLog.i("查询某人的任务3失败");
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                }//else


            }//done
        });// query1.findObjs
    }//queryMission
}//MissionAct_cls
