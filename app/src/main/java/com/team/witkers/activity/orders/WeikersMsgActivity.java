package com.team.witkers.activity.orders;

import android.os.Bundle;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.adapter.MsgOrdersBeanAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ChooseClaimant;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.bean.MyUser;
import com.team.witkers.fragment.msgfrm.RecyclerViewAdapter;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by lenovo on 2016/12/5.
 */

public class WeikersMsgActivity extends BGAPPToolbarActivity {

    private ProgressDialog mDialog;
    private static final int STATE_FIRST = 0;// 第一次载入
    private static final int STATE_REFRESH = 1;// 下拉刷新
    private static final int STATE_MORE = 2;// 加载更多
    private MsgOrdersBeanAdapter msgOrdersBeanAdapter;

    private List<MsgOrdersBean> dataListMsgBean = new ArrayList<MsgOrdersBean>();
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_weikers_notify);
//        MyLog.v("WeikersMsgAct_init");
        mPullLoadMoreRecyclerView= (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setLinearLayout();
        //设置下拉刷新颜色
        mPullLoadMoreRecyclerView.setColorSchemeResources(R.color.swipe_refresh_color);
    }

    @Override
    protected void setListener() {
        setTitle("微客消息");
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        queryChooseClaimant(0, STATE_FIRST);
    }

    private void queryChooseClaimant(int page, final int actionType) {

        MyUser mUser = MyApplication.mUser;

        BmobQuery<Mission> query = new BmobQuery<>();
        query.addWhereEqualTo("claimName", mUser.getUsername());

        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> selectedList, BmobException e) {
                if (e == null) {
                    MyLog.i("查询选定接单人成功");
                    if (selectedList.size() != 0) {

                        MyLog.d("selectList_size not 0");

                        if (actionType == STATE_MORE) {
                            msgOrdersBeanAdapter.notifyDataSetChanged();
                        } else {//刷新或初始化
//                            lastTime=pubMissionList.get(pubMissionList.size()-1).getCreatedAt();
                            dataListMsgBean = new ArrayList<MsgOrdersBean>();
                            dataListMsgBean = getBeanList(selectedList);
                            msgOrdersBeanAdapter = new MsgOrdersBeanAdapter(WeikersMsgActivity.this, dataListMsgBean);
                            mPullLoadMoreRecyclerView.setAdapter(msgOrdersBeanAdapter);
//                                        }else{
                            //切换账号时，刷新只执行 not null，导致数据无法 正确刷新
//                                            MyLog.e("adapter not null");
//                                            msgOrdersBeanAdapter.notifyDataSetChanged();
//                                        }

                        }// 刷新或初始化


                        MyLog.i("去掉对话框，去掉刷新");
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();


                    } else
                        MyLog.d("list_size_ 0");

                } else {
                    MyLog.i("查询选定接单人失败_ " + e.getMessage());
                }
            }//done
        });//query.findObjs
    }//queryClaimant

    //将数据封装到 MsgOrdersBean里
    private List<MsgOrdersBean> getBeanList(List<Mission> list) {
        MyLog.i("getBeanList里面");
        List<MsgOrdersBean> beanList = new ArrayList<MsgOrdersBean>();

        for (Mission mission : list) {
              MyLog.i("pubUserName_ "+mission.getPubUserName()+"/// isFinished_ "+mission.getFinished());
            //一个mission 选定任务需要分为有 4、5 两种 类型，
            // 0.没人认领的，跳过封装；  mission.getClaimItemList()==null
            // 1.有认领人数的，没有确定认领人的；  mission.getChooseClaimant()==null(大多数) ,msgOrdersBean.getClaimFlag=1
            // 2.有确定认领人的，任务正在进行中的； mission.isFinished==false ,msgOrdersBean.getClaimFlag=2
            // 3.有确定认领人的，任务已经完成的；   mission.isFinished==true ,msgOrdersBean.getClaimFlag=3

//            6. 有人选择了你， 接单任务
//            7.你完成了别人 指定的任务/


//            6.有人选择了你， 接单任务 正在进行中  把content设置成pubUserName
            if(!mission.getFinished()){

                String pubUserName=mission.getPubUserName();
                String time =mission.getFinishTime();
//                MyLog.i("takerNum--->"+takerNum+"time--->"+mission.getClaimItemList().get(takerNum-1).getClaimTime());
                String headUrl = mission.getClaimItemList().get(0).getClaimHeadUrl();

                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(pubUserName,1,headUrl,time,null,"",6);
                beanList.add(msgOrdersBean);
            }else{
//            7.你完成了别人 指定的任务/
                String content=mission.getInfo();
                String time =mission.getFinishTime();
                String headUrl = mission.getClaimItemList().get(0).getClaimHeadUrl();

                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(content,1,headUrl,time,null,"",7);
                beanList.add(msgOrdersBean);


            }//else



        }// for
        return beanList;
    }//getBeanList

}//WeikersMsgAct_cls
