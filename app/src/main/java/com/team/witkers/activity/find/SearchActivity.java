package com.team.witkers.activity.find;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.Lable;
import com.team.witkers.fragment.searchfrm.AllSearchFragment;
import com.team.witkers.fragment.searchfrm.MissionSearchFragment;
import com.team.witkers.fragment.searchfrm.UserSearchFragment;
import com.team.witkers.utils.MyToast;
import com.team.witkers.views.ClearEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ClearEditText et_lable_search;
    List<Fragment> fragments = new ArrayList<>();
    List<String > titles = new ArrayList<>();
    List<Lable> lableList = new ArrayList<>();
    private String lableStr;
    private AllSearchFragment allSearchFragment;

    @Override
    protected int setContentId() {
        return R.layout.activity_hot_lable_details_2;
    }

    @Override
    protected void getIntentData() {
         lableStr = getIntent().getStringExtra("ToHotLableDetails");
//        MyToast.showToast(this,"get the lable -->"+lableStr);
    }

    @Override
    protected void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewPager_test);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout =(TabLayout)findViewById(R.id.tabLayout_test);
        et_lable_search = (ClearEditText) findViewById(R.id.et_lable_search);

        fragments.add(new AllSearchFragment());
        fragments.add(new MissionSearchFragment());
        fragments.add(new UserSearchFragment());

        titles.add("全部搜索");
        titles.add("标签");
        titles.add("用户");
    }

    @Override
    protected void showView() {
        et_lable_search.setText(lableStr);

        for (int i =0;i<titles.size();i++){
            Log.d("Test",titles.size()+"");
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
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

//     if(lableStr.trim()!=null&&!lableStr.equals("")){
//            allSearchFragment = new AllSearchFragment();
//            et_lable_search.setText(lableStr);
//            allSearchFragment.searchPerson(lableStr);
//        }
    }


}
