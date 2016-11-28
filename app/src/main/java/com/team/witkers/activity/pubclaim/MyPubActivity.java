package com.team.witkers.activity.pubclaim;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.team.witkers.R;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.adapter.MissionStateAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.fragment.MyPubFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by hys on 2016/11/4.
 */


public class MyPubActivity extends BaseActivity {

    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    List<Fragment> fragments = new ArrayList<>();
    List<String > titles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MissionStateAdapter mAdapter;

    @Override
    protected int setContentId() {
        return R.layout.activity_mypub_2;
    }

    @Override
    protected void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mTabLayout =(TabLayout)findViewById(R.id.tabLayout);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("我的发布");
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initData() {
        MyPubFragment allOrdersFrm=new MyPubFragment();
        MyPubFragment doingFrm=new MyPubFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean("isDoing",true);
        doingFrm.setArguments(bundle);


        fragments.add(allOrdersFrm);
        fragments.add(doingFrm);

        titles.add("全部订单");
        titles.add("待完成");

        for (int i =0;i<titles.size();i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);

    }//initData

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
