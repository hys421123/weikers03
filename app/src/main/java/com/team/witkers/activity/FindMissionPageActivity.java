package com.team.witkers.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.team.witkers.R;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.Mission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hys on 2016/10/21.
 */

public class FindMissionPageActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private List<Mission> missoinList = new ArrayList<>();
    private MissionAdapter mAdapter;
    private String label;
    @Override
    protected int setContentId() {
        return R.layout.activity_find_mission_page;
    }

    @Override
    protected void initView() {

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView= (RecyclerView) findViewById(R.id.mRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }
    @Override
    protected void initToolBar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("#"+label+"#");
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
    protected void getIntentData() {

        label=getIntent().getStringExtra("fromFindMissoionLabel");
        missoinList=(ArrayList<Mission>) getIntent().getSerializableExtra("fromTakeOutMissionAdapterTV");
//        MyLog.i("getIntent  missionSize---->"+missoinList.size());
//        MyLog.d("missionDetails--> "+missoinList.get(2).getInfo());
    }

    @Override
    protected void initData() {
        mAdapter = new MissionAdapter(this,missoinList);
        mRecyclerView.setAdapter(mAdapter );
    }
}
