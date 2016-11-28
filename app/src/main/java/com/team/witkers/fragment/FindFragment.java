package com.team.witkers.fragment;

import android.content.Intent;
import android.view.View;

import com.team.witkers.R;
import com.team.witkers.activity.LoginActivity;
import com.team.witkers.activity.find.HotLableActivity;
import com.team.witkers.activity.find.TendencyActivity;
import com.team.witkers.activity.find.UserComplaintActivity;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.MyUser;
import com.team.witkers.views.MyPubItem;

import cn.bmob.v3.BmobUser;


public class FindFragment extends BaseFragment implements View.OnClickListener
{

	private MyPubItem customView_like_tendencies,customView_like_hotLabel,
			customView_like_advice,customView_like_events,customView_like_nearWke;

	@Override
	protected int setContentId() {
		return R.layout.fragment_find_2;
	}

	@Override
	protected void initView(View view) {
		customView_like_tendencies= (MyPubItem) view.findViewById(R.id.customView_like_tendencies);
		customView_like_hotLabel= (MyPubItem) view.findViewById(R.id.customView_like_hotLabel);
		customView_like_advice = (MyPubItem) view.findViewById(R.id.customView_like_advice);
		customView_like_events = (MyPubItem) view.findViewById(R.id.customView_like_events);
		customView_like_nearWke = (MyPubItem) view.findViewById(R.id.customView_like_nearWke);
	}

	@Override
	protected void setListener() {
		customView_like_tendencies.setOnClickListener(this);
		customView_like_hotLabel.setOnClickListener(this);
		customView_like_advice.setOnClickListener(this);
		customView_like_events.setOnClickListener(this);
		customView_like_nearWke.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.customView_like_tendencies:
				if(BmobUser.getCurrentUser(MyUser.class)==null){//若没有登录，直接跳转登录界面
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}else {
					Intent intent = new Intent(getActivity(), TendencyActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.customView_like_hotLabel:
				if(BmobUser.getCurrentUser(MyUser.class)==null){//若没有登录，直接跳转登录界面
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}else {
					Intent intent = new Intent(getActivity(), HotLableActivity.class);
					startActivity(intent);
				}
				break;

			case R.id.customView_like_advice:
				if(BmobUser.getCurrentUser(MyUser.class)==null){//若没有登录，直接跳转登录界面
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}else {
					Intent intent = new Intent(getActivity(), UserComplaintActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.customView_like_events:
				break;
			case R.id.customView_like_nearWke:
				break;
		}//switch
	}//onClick
}
