package com.team.witkers.fragment.msgfrm;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.adapter.MsgOrdersBeanAdapter;
import com.team.witkers.adapter.MsgTendAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.OrdersMsgEvent2;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.NetworkUtils;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dell on 2016/11/21.
 */

public class OrdersFragment2 extends BaseFragment implements PullLoadMoreRecyclerView.PullLoadMoreListener{
    private MyUser myUser;
    private MsgOrdersBeanAdapter msgOrdersBeanAdapter;
    private  PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private int mCount = 1;
    private List<MsgOrdersBean> dataListMsgBean=new ArrayList<MsgOrdersBean>();

    private int lastVisibleItem;
    private ProgressDialog mDialog;
    private static final int STATE_FIRST = 0;// 第一次载入
    private static final int STATE_REFRESH = 1;// 下拉刷新
    private static final int STATE_MORE = 2;// 加载更多
    private static final int STATE_NONE = 3;//adapter为空时
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";   //最后一条的时间
    private int LIMIT = 6;        // 每页的数据是10条
    @Override
    protected int setContentId() {
        return R.layout.fragment_message_orders3;
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
        //设置下拉刷新颜色
        mPullLoadMoreRecyclerView.setColorSchemeResources(R.color.swipe_refresh_color);

    }

    @Override
    protected void setListener() {
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
    }

    @Override
    protected void loadDataAfterView() {
        if(MyApplication.mUser==null){
            MyToast.showToast(getActivity(),"亲，请先登录好吗");
            return;
        }
        mDialog = new ProgressDialog(getActivity(), "正在加载");
        mDialog.show();
        queryBmobData(0,STATE_FIRST);

    }

    private void queryBmobData(int page, final int actionType) {

        // 查询这个用户认领的认领的所有mission
        BmobQuery<Mission> query1 = new BmobQuery<Mission>();
        query1.order("-createdAt");
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query1.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 跳过之前页数并去掉重复数据
            query1.setSkip(page * LIMIT + 1);
        } else {
            page = 0;
            query1.setSkip(page);
        }

        // 设置每页数据个数
        query1.setLimit(LIMIT);
        //设置缓存机制，有网络时为网络优先，无网络时缓存优先
        boolean isCache = query1.hasCachedResult(Mission.class);
        if(NetworkUtils.isNetWorkConnet(getActivity()))
            query1.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        else
            query1.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
        query1.addWhereRelatedTo("takeMissions", new BmobPointer(myUser));
        query1.findObjects(new FindListener<Mission>() {
            @Override
            public void done(final List<Mission> takeMissionList, BmobException e) {
                if(e==null){
                    MyLog.i("查询任务成功-我是认领者-》"+takeMissionList.size());
                    //查询这个用户作为发布者
                    BmobQuery<Mission> query=new BmobQuery<>();
                    query.order("-createdAt");
                    MyLog.v("myUserName_ "+myUser.getUsername());
                    query.addWhereEqualTo("pubUserName",myUser.getUsername());
                    query.findObjects(new FindListener<Mission>() {
                        @Override
                        public void done(List<Mission> pubMissionList, BmobException e) {
                            if(e==null){
                                MyLog.i("查询任务成功-我是发布者-》"+pubMissionList.size());

                                if(pubMissionList.size()==0&&takeMissionList.size()==0){
                                    MyLog.i("list size为零");
//                                    msgOrdersBeanAdapter = null;
//                                    mPullLoadMoreRecyclerView.setAdapter(msgOrdersBeanAdapter);
//                                    MyLog.i("去掉对话框，去掉刷新");
                                    if(mDialog!=null){ mDialog.dismiss();}
//                                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                                    return;
                                }else {//size not 0
                                    MyLog.i("list size大于零");

                                    if(actionType==STATE_MORE){
                                        msgOrdersBeanAdapter.notifyDataSetChanged();
                                    }else{//刷新或初始化
                                        lastTime=pubMissionList.get(pubMissionList.size()-1).getCreatedAt();
                                        dataListMsgBean=new ArrayList<MsgOrdersBean>();
                                        dataListMsgBean = getBeanList(pubMissionList);
//                                        if(msgOrdersBeanAdapter==null){
//                                            MyLog.e("adapter null");
                                            msgOrdersBeanAdapter=new  MsgOrdersBeanAdapter(getActivity(),dataListMsgBean);
                                            mPullLoadMoreRecyclerView.setAdapter(msgOrdersBeanAdapter);
//                                        }else{
                                        //切换账号时，刷新只执行 not null，导致数据无法 正确刷新
//                                            MyLog.e("adapter not null");
//                                            msgOrdersBeanAdapter.notifyDataSetChanged();
//                                        }

                                    }// 刷新或初始化


                                    MyLog.i("去掉对话框，去掉刷新");
                                    if(mDialog!=null){ mDialog.dismiss();}
                                   mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                                }//size>0
                            }else{ //e!=null,查询失败
                                MyLog.e("查询订单消息失败"+e.getMessage());
                                if(mDialog!=null){ mDialog.dismiss();}
                                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                            }//else
                        }//内部done
                    });
                }else{
                    MyLog.i("失败："+e.getMessage());
                }//else
            }//外部done
        });
    }//queryBmobData

    //将数据封装到 MsgOrdersBean里
    private List<MsgOrdersBean>  getBeanList(List<Mission> list){
        MyLog.i("getBeanList里面");
        List<MsgOrdersBean> beanList = new ArrayList<MsgOrdersBean>();

//         把微客消息处理 装进去
        String content0="微客消息通知";

        int takerNum0=0;
        String headUrl0="";
        String time0="";

        MsgOrdersBean msgOrdersBean0 = new MsgOrdersBean(content0,takerNum0,headUrl0,time0,null,null,0);
        beanList.add(msgOrdersBean0);

        for(Mission mission:list){
            //一个mission 发布任务需要分为有三种分类，
            // 0.没人认领的，跳过封装；  mission.getClaimItemList()==null
            // 1.有认领人数的，没有确定认领人的；  mission.getChooseClaimant()==null(大多数) ,msgOrdersBean.getClaimFlag=1
            // 2.有确定认领人的，任务正在进行中的； mission.isFinished==false ,msgOrdersBean.getClaimFlag=2
            // 3.有确定认领人的，任务已经完成的；   mission.isFinished==true ,msgOrdersBean.getClaimFlag=3

//            4. 有人选择了你， 接单任务
//            5.你完成了别人 指定的任务/


            //第0种
            if(mission.getClaimItemList()==null){//若没有认领人，则跳过封装
                continue;
            }
            //第1种,有认领人数，没确定具体认领人
            if(mission.getChooseClaimant()==null){
//                MyLog.e("有人认领");

                List<ClaimItems> claimItemsList =mission.getClaimItemList();
                String missionId = mission.getObjectId();
                int  takerNum = mission.getClaimItemList().size();
                String content=mission.getInfo();
//                MyLog.e(content);
                String time =mission.getClaimItemList().get(takerNum-1).getClaimTime();
//                MyLog.i("takerNum--->"+takerNum+"time--->"+mission.getClaimItemList().get(takerNum-1).getClaimTime());
                String headUrl = mission.getClaimItemList().get(0).getClaimHeadUrl();
//                MyLog.i("headUrl--->"+headUrl+"time---->"+mission.getClaimItemList().get(0).getClaimTime());
                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(content,takerNum,headUrl,time,claimItemsList,missionId,1);
                beanList.add(msgOrdersBean);
            }

            //第2种，确定认领人，任务正在进行中
            if(mission.getChooseClaimant()!=null&&mission.getFinished() ==false){
                String missionId = mission.getObjectId();
                String content=mission.getInfo();
                String time =mission.getFinishTime();
//                MyLog.i("takerNum--->"+takerNum+"time--->"+mission.getClaimItemList().get(takerNum-1).getClaimTime());
                String headUrl = mission.getClaimItemList().get(0).getClaimHeadUrl();
//                MyLog.i("headUrl--->"+headUrl+"time---->"+mission.getClaimItemList().get(0).getClaimTime());
                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(content,1,headUrl,time,null,missionId,2);
                beanList.add(msgOrdersBean);
            }

            //第三种，确定认领人，任务已完成
            if(mission.getFinished()==true){
                String missionId = mission.getObjectId();
                String content=mission.getInfo();
                String time =mission.getFinishTime();
//                MyLog.i("takerNum--->"+takerNum+"time--->"+mission.getClaimItemList().get(takerNum-1).getClaimTime());
                String headUrl = mission.getClaimItemList().get(0).getClaimHeadUrl();
//                MyLog.i("headUrl--->"+headUrl+"time---->"+mission.getClaimItemList().get(0).getClaimTime());
                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(content,1,headUrl,time,null,missionId,3);
                beanList.add(msgOrdersBean);
            }


//            else{
//                //若任务有人认领
//                List<ClaimItems> claimItemsList =mission.getClaimItemList();
//                /////////////////////////////////////
////                Boolean selectFlag = isSelected(claimItemsList);
//                if(!selectFlag){//没有确定认领微客
//                    MyLog.i("没有确定认领微客");
//
//                    if(claimItemsList.get(claimPosition).getClaimName().equals(myUser.getUsername())){
//                        MyLog.i("我是认领者");
//                    }else if(mission.getPubUserName().equals(myUser.getUsername())){
//                        MyLog.i("我是发布者");
//                        String missionId = mission.getObjectId();
//                        int  takerNum = mission.getClaimItemList().size();
//                        String content=mission.getInfo();
//                        String time =mission.getClaimItemList().get(takerNum-1).getClaimTime();
//                        MyLog.i("takerNum--->"+takerNum+"time--->"+mission.getClaimItemList().get(takerNum-1).getClaimTime());
//                        String headUrl = mission.getClaimItemList().get(0).getClaimHeadUrl();
//                        MyLog.i("headUrl--->"+headUrl+"time---->"+mission.getClaimItemList().get(0).getClaimTime());
//                        MsgOrdersBean msgOrdersBean = new MsgOrdersBean(
//                                content,takerNum,headUrl,time,claimItemsList,missionId,"kind1");
//                        beanList.add(msgOrdersBean);
//                    }else{
//                        MyLog.i("我是无关人员");
//                    }
//                }else {
//                    switch (claimItemsList.get(claimPosition).getClaimFlag()){
//                        case "Doing":
//                            MyLog.i("Doing");
//                            MyLog.i("takerName-->"+claimItemsList.get(claimPosition).getClaimName()+
//                                    "--claimMoney-->"+claimItemsList.get(claimPosition).getClaimMoney()+
//                                    "--missionInfo-->"+mission.getInfo());
//
//                            if(claimItemsList.get(claimPosition).getClaimName().equals(myUser.getUsername())){
//                                MyLog.i("我是认领者---xxx选择了你");
//                                String missionId = mission.getObjectId();
//                                String content=mission.getPubUserName();
//                                String headUrl = mission.getPubUserHeadUrl();
//                                MyLog.i("time qian"+mission.getDealTime());
//                                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
//                                String time = formatter.format(mission.getDealTime());
//                                MyLog.i("time hou");
//                                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(
//                                        content,0,headUrl,time,claimItemsList,missionId,"kind2");
//                                beanList.add(msgOrdersBean);
//                            }else if(mission.getPubUserName().equals(myUser.getUsername())){
//                                MyLog.i("我是发布者---任务正在进行中");
//                                String headUrl = mission.getClaimItemList().get(claimPosition).getClaimHeadUrl();
//                                MyLog.i("headUrl-->"+headUrl
//                                        +",claimPosition-->"+claimPosition
//                                        +",claimList-->"+mission.getClaimItemList().get(claimPosition).getClaimName());
//                                String content = mission.getInfo();
//                                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
//                                String time = formatter.format(mission.getDealTime());
//                                String missionId = mission.getObjectId();
//                                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(
//                                        content,0,headUrl,time,claimItemsList,missionId,"kind3");
//                                beanList.add(msgOrdersBean);
//                            }else{
//                                MyLog.i("我是无关人员");
//                            }
//                            break;
//                        case "After":
//                            MyLog.i("After");
//                            if(claimItemsList.get(claimPosition).getClaimName().equals(myUser.getUsername())){
//                                MyLog.i("我是认领者---等待对方评价");
//                                String missionId = mission.getObjectId();
//                                String content=mission.getInfo();
//                                String headUrl = mission.getPubUserHeadUrl();
//                                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
//                                String time = formatter.format(mission.getDealTime());
//                                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(
//                                        content,0,headUrl,time,claimItemsList,missionId,"kind4");
//                                beanList.add(msgOrdersBean);
//                            }else if(mission.getPubUserName().equals(myUser.getUsername())){
//                                MyLog.i("我是发布者---已完成，请评价");
//                                String headUrl = mission.getClaimItemList().get(claimPosition).getClaimHeadUrl();
//                                String content = mission.getInfo();
//                                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
//                                String time = formatter.format(mission.getDealTime());
//                                String missionId = mission.getObjectId();
//                                MsgOrdersBean msgOrdersBean = new MsgOrdersBean(
//                                        content,0,headUrl,time,claimItemsList,missionId,"kind5");
//                                beanList.add(msgOrdersBean);
//                            }else{
//                                MyLog.i("我是无关人员");
//                            }
//                            break;
//                    }
//                }
//            }
        }//for
        return beanList;
    }


//    private void getData() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mRecyclerViewAdapter.addAllData(setList());
//                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
//                    }
//                });
//
//            }
//        }, 1000);
//
//    }



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
        queryBmobData(0, STATE_REFRESH);

    }

    // 加载更多
    @Override
    public void onLoadMore() {
        MyLog.v("onLoadMore");
        queryBmobData(0, STATE_MORE);
    }


}
