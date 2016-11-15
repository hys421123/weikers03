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

import com.team.witkers.R;
import com.team.witkers.fragment.msgfrm.MessagesFragment;
import com.team.witkers.fragment.msgfrm.OrdersFragment;

import java.util.ArrayList;
import java.util.List;

public class MsgFragment extends Fragment {

	private ViewPager mViewPager;
	private TabLayout mTabLayout;
/*	Paint dividerPaint;
	private int dividerColor = 0x00000000;// 分割线的颜色*/

	List<Fragment> fragments = new ArrayList<>();
	List<String > titles = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_msg, container, false);


		mViewPager = (ViewPager)view.findViewById(R.id.viewPager_test);
		mViewPager.setOffscreenPageLimit(2);
		mTabLayout =(TabLayout)view.findViewById(R.id.tabLayout_test);
/*		dividerPaint = new Paint();*/


		fragments.add(new OrdersFragment());
		fragments.add(new MessagesFragment());

		titles.add("订单");
		titles.add("动态");

/*		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();//获得屏幕宽度*/

		for (int i =0;i<titles.size();i++){
			Log.d("Test",titles.size()+"");
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
