package com.team.witkers.fragment.msgfrm;

import android.os.Handler;
import android.view.View;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.OrdersMsgEvent2;
import com.team.witkers.utils.MyToast;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by dell on 2016/11/21.
 */

public class OrdersFragment2 extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener{
    private MyUser myUser;

    private  PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private int mCount = 1;

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
        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity());
        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        getData();
    }

    private List<String> setList() {
        List<String> dataList = new ArrayList<>();
        int start = 20 * (mCount - 1);
        for (int i = start; i < 20 * mCount; i++) {
            dataList.add("Second" + i);
        }
        return dataList;

    }

    private void getData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.addAllData(setList());
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }
                });

            }
        }, 1000);

    }

    public void clearData() {
        mRecyclerViewAdapter.clearData();
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEventMainThread(OrdersMsgEvent2 event) {

        MyLog.i("有数据发生了改变22 ----》");
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

    //刷新
    @Override
    public void onRefresh() {

        MyLog.v("onRefresh");
        setRefresh();
        getData();
    }

    // 加载更多
    @Override
    public void onLoadMore() {
        MyLog.v("onLoadMore");
        mCount = mCount + 1;
        getData();
    }

    private void setRefresh() {
        mRecyclerViewAdapter.clearData();
        mCount = 1;
    }
}
