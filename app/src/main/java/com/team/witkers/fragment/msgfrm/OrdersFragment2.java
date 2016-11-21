package com.team.witkers.fragment.msgfrm;

import android.view.View;

import com.team.witkers.R;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;

/**
 * Created by dell on 2016/11/21.
 */

public class OrdersFragment2 extends BaseFragment {
    private MyUser myUser;

    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;


    @Override
    protected int setContentId() {
        return R.layout.fragment_message_orders2;
    }

    @Override
    protected void initDataBeforeView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView(View view) {
       mPullLoadMoreRecyclerView= (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setLinearLayout();

    }



    @Override
    public void onResume() {
        super.onResume();
        myUser = BmobUser.getCurrentUser(MyUser.class);
        if(myUser==null){
            MyToast.showToast(getActivity(),"请你登陆");
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
