package com.team.witkers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.adapter.MissionStateAdapter;
import com.team.witkers.base.BaseFragment;
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

public class MyPubFragment extends BaseFragment {

//标志位 若为待完成，
    private boolean isDoing=false;
    private RecyclerView mRecyclerView;
    private List<Mission> dataList = new ArrayList<>();
    private int LIMIT = 8;        // 每页的数据是8条
    private LinearLayoutManager linearLayoutManager;
    private MissionStateAdapter mAdapter;
    private String lastTime = "";
    @Override
    protected int setContentId() {
        return R.layout.fragment_mypub;
    }

    @Override
    protected void getArguments0() {
      Bundle bundle=  getArguments();
        if(bundle!=null)
            isDoing= bundle.getBoolean("isDoing");
    }

    @Override
    protected void initView(View view) {
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_takeoutfrm);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void loadDataAfterView() {

        BmobQuery<Mission> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        //返回8条数据，
        query.setLimit(LIMIT);

        //执行查询方法
        boolean isCache = query.hasCachedResult(Mission.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }

        query.addWhereEqualTo("pubUser", MyApplication.mUser);
        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> object, BmobException e) {
                if(e==null){
                    if (object.size() == 0) {
                        MyToast.showToast(getActivity(),"没有数据,快去发布吧!");
//                        if (mDialog != null) {
//                            mDialog.dismiss();
//                        }
                        return;
                    }
                    // TODO Auto-generated method stub
                    lastTime = object.get(object.size() - 1).getCreatedAt();
                    // Log.d("lastTime", lastTime);
                    // 将本次查询的数据添加到lostList中
                    for (Mission lb : object) {
//                        MyLog.i("pubUsername_ "+lb.getPubUser().getUsername());
                        //是否为待完成
                        if(isDoing){
                            if(lb.getChooseClaimant()!=null&&!lb.getFinished()){
                                dataList.add(lb);
                            }else
                                continue;
                        }else
                            dataList.add(lb);
                    }
                    mAdapter = new MissionStateAdapter(getActivity(),dataList);
                    mRecyclerView.setAdapter(mAdapter );

//                    progressWheel.stopSpinning();
                }else{
                    MyLog.e("查询失败_ " + e.getMessage());
//                    progressWheel.stopSpinning();
                }//else
            }//done
        });
    }//loadAfterView
}//MyPubFrm
