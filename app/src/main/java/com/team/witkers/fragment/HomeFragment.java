package com.team.witkers.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.fragment.homefrm.AllFragment5;
import com.team.witkers.fragment.homefrm.ExpressFragment1;
import com.team.witkers.fragment.homefrm.HotBoomFragment2;
import com.team.witkers.fragment.homefrm.MoveHouseFragment3;
import com.team.witkers.fragment.homefrm.RentCarFragment4;

import com.team.witkers.fragment.homefrm.TakeOutFragment0;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;


public class HomeFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private BmobGeoPoint myPoint;

    List<Fragment> fragments = new ArrayList<>();
    List<String > titles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);

        mViewPager = (ViewPager)view.findViewById(R.id.viewPager);
        //viewpager缓存页面数量
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout =(TabLayout)view.findViewById(R.id.tabLayout);

        myPoint = (BmobGeoPoint) getArguments().getSerializable("myPoint");
//        MyLog.i("homeFragment----latitude->"+myPoint.getLatitude()+",longitude->"+myPoint.getLongitude());

        Bundle bundle = new Bundle();
        bundle.putSerializable("myPoint",myPoint);

        AllFragment5 allFragment5 = new AllFragment5();
        allFragment5.setArguments(bundle);
        TakeOutFragment0 takeOutFragment0 = new TakeOutFragment0();
        takeOutFragment0.setArguments(bundle);
        ExpressFragment1 expressFragment1 = new ExpressFragment1();
        expressFragment1.setArguments(bundle);
        HotBoomFragment2 hotBoomFragment2 = new HotBoomFragment2();
        hotBoomFragment2.setArguments(bundle);
        MoveHouseFragment3 moveHouseFragment3 = new MoveHouseFragment3();
        moveHouseFragment3.setArguments(bundle);
        RentCarFragment4 rentCarFragment4 = new RentCarFragment4();
        rentCarFragment4.setArguments(bundle);


        fragments.add(allFragment5);
        fragments.add(takeOutFragment0);
        fragments.add(expressFragment1);
        fragments.add(hotBoomFragment2);
        fragments.add(moveHouseFragment3);
        fragments.add(rentCarFragment4);
        titles.add("全部");
        titles.add("外卖");
        titles.add("快递");
        titles.add("代购");
        titles.add("搬家");
        titles.add("租车");

        for (int i =0;i<titles.size();i++){
            Log.d("size",titles.size()+"");
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
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

        return view;
    }
}
