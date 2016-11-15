package com.team.witkers.fragment.msgfrm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.adapter.MsgOrdersBeanAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.OrdersMsgEvent2;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.NetworkUtils;
import com.zfdang.multiple_images_selector.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by shanyao on 2016/6/17.
 */
public class OrdersFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private TextView tvTime,tvDetails;
    private ImageView ivPubUser;
    private  RelativeLayout rlItem;
    private RecyclerView mRecyclerView;
    private MsgOrdersBeanAdapter msgOrdersBeanAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<MsgOrdersBean> dataListMsgBean=new ArrayList<MsgOrdersBean>();
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private ProgressDialog mDialog;
    private static final int STATE_FIRST = 0;// 第一次载入
    private static final int STATE_REFRESH = 1;// 下拉刷新
    private static final int STATE_MORE = 2;// 加载更多
    private static final int STATE_NONE = 3;//adapter为空时
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";   //最后一条的时间
    private int LIMIT = 6;        // 每页的数据是10条
    private MyUser myUser;
//    private static final int NOTIFICATION_FLAG = 1;
    private NotificationManager notificationManager;

    private  int claimPosition;//认领用户在claimList中的位置

    @Override
    protected int setContentId() {
        return R.layout.fragment_message_orders;
    }

    @Override
    protected void initDataBeforeView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView(View view) {

        tvDetails = (TextView) view.findViewById(R.id.tv_details);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        ivPubUser = (ImageView) view.findViewById(R.id.iv_pubUser);
        rlItem = (RelativeLayout) view.findViewById(R.id.rel_item);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_item);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(90,173,241));
//        dataListOrdersMsg = new ArrayList<OrdersMsg>();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),linearLayoutManager.getOrientation(),0));
    }



    @Override
    protected void setListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int count;
                MyLog.i("onScrollStateChanged");
                if (msgOrdersBeanAdapter == null) {
                    MyLog.i("mAdapter_null");
                    queryBmobData(0, STATE_NONE);
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == msgOrdersBeanAdapter.getItemCount()){
//                    MyToast.showToast(getActivity(), "Scroll to refresh");
                    MyLog.i("SCROLL_STATE_IDLE");
                    MyLog.i("lastTime-->"+lastTime);
                    queryBmobData(curPage, STATE_MORE);
                }//if
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                MyLog.i("onScrolled");
//                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    protected void loadDataAfterView() {
        mDialog = new ProgressDialog(getActivity(), "正在加载");
        mDialog.show();
        loadDataForFirstTime();
    }//loadDataAfterView

    @Override
    public void onResume() {
        super.onResume();
        myUser = BmobUser.getCurrentUser(MyUser.class);
        if(myUser==null){
            MyToast.showToast(getActivity(),"请你登陆");
            return;
        }
    }

    private void loadDataForFirstTime(){
        if(MyApplication.mUser==null){//登陆检查
            MyToast.showToast(getActivity(),"亲，请先登录好吗");
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if(msgOrdersBeanAdapter ==null){
//                dataList.clear();
//                msgOrdersBeanAdapter.notifyDataSetChanged();
                MyLog.i("query里面也执行了吗");
            }
            if(mDialog!=null)
                mDialog.dismiss();
            return;
        }else{
            myUser = BmobUser.getCurrentUser(MyUser.class);
        }
        BmobQuery<Mission> query1 = new BmobQuery<Mission>();
        query1.order("-createdAt");
        boolean isCache = query1.hasCachedResult(Mission.class);
        if(isCache){
            query1.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query1.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        query1.addWhereRelatedTo("takeMissions", new BmobPointer(myUser));
        query1.findObjects(new FindListener<Mission>() {
            @Override
            public void done(final List<Mission> takeMissionList, BmobException e) {
                if(e==null){
                    MyLog.i("查询任务成功-我是认领者-》"+takeMissionList.size());
                    //查询这个用户作为发布者
                    BmobQuery<Mission> query=new BmobQuery<>();
                    query.order("-createdAt");
                    query.addWhereEqualTo("pubUserName",myUser.getUsername());
                    query.findObjects(new FindListener<Mission>() {
                        @Override
                        public void done(List<Mission> pubMissionList, BmobException e) {
                            if(e==null){
//                                MyLog.i("查询任务成功-我是发布者-》"+pubMissionList.size());
//                                MyLog.i("查询任务成功-我是认领者-》"+takeMissionList.size());
                                if(pubMissionList.size()==0&&takeMissionList.size()==0){
                                    MyLog.i("list size为零");
                                    msgOrdersBeanAdapter = null;
                                    mRecyclerView.setAdapter(msgOrdersBeanAdapter);
                                    MyLog.i("去掉对话框，去掉刷新");
                                    if(mDialog!=null){ mDialog.dismiss();}
                                    if (mSwipeRefreshLayout != null) {mSwipeRefreshLayout.setRefreshing(false);}
                                    return;
                                }else {
                                    MyLog.i("list size大于零");
                                    MyLog.i("第一次加载数据");
                                    List<Mission> missionList = takeMissionList;
                                    if (pubMissionList != null) {
                                        for (int i = 0; i < pubMissionList.size(); i++) {
                                            missionList.add(pubMissionList.get(i));
                                        }
                                    }
                                    lastTime = missionList.get(missionList.size() - 1).getCreatedAt();
                                    MyLog.i("两个list的和----》" + missionList.size());
                                    dataListMsgBean = getBeanList(missionList);
                                    msgOrdersBeanAdapter = new MsgOrdersBeanAdapter(getActivity(), dataListMsgBean);
                                    mRecyclerView.setAdapter(msgOrdersBeanAdapter);
                                    MyLog.i("去掉对话框，去掉刷新");
                                    if(mDialog!=null){ mDialog.dismiss();}
                                    if (mSwipeRefreshLayout != null) {mSwipeRefreshLayout.setRefreshing(false);}
                                }
                            }else{
                                MyLog.e("查询订单消息失败"+e.getMessage());
                                if(mDialog!=null){ mDialog.dismiss();}
                                if (mSwipeRefreshLayout != null) {mSwipeRefreshLayout.setRefreshing(false);}
                            }//else
                        }//内部done
                    });
                }else{
                    MyLog.i("失败11："+e.getMessage());
                    if(mDialog!=null)
                        mDialog.dismiss();
                }//else
            }//外部done
        });
    }


    private void queryBmobData(int page, final int actionType) {
        if(MyApplication.mUser==null){//登陆检查
            MyToast.showToast(getActivity(),"亲，请先登录好吗");
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if(msgOrdersBeanAdapter ==null){
//                dataList.clear();
//                msgOrdersBeanAdapter.notifyDataSetChanged();
                MyLog.i("query里面也执行了吗");
            }
            if(mDialog!=null)
                mDialog.dismiss();
            return;
        }
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
                    query.addWhereEqualTo("pubUserName",myUser.getUsername());
                    query.findObjects(new FindListener<Mission>() {
                        @Override
                        public void done(List<Mission> pubMissionList, BmobException e) {
                            if(e==null){
                                MyLog.i("查询任务成功-我是发布者-》"+pubMissionList.size());
                                MyLog.i("查询任务成功-我是认领者-》"+takeMissionList.size());
                                if(pubMissionList.size()==0&&takeMissionList.size()==0){
                                    MyLog.i("list size为零");
                                    msgOrdersBeanAdapter = null;
                                    mRecyclerView.setAdapter(msgOrdersBeanAdapter);
                                    MyLog.i("去掉对话框，去掉刷新");
                                    if(mDialog!=null){ mDialog.dismiss();}
                                    if (mSwipeRefreshLayout != null) {mSwipeRefreshLayout.setRefreshing(false);}
                                    return;
                                }else {
                                    MyLog.i("list size大于零");
                                    if (actionType == STATE_REFRESH){//刷新界面
                                        MyLog.i("刷新数据");
                                        List<Mission> missionList = takeMissionList;
                                        if(pubMissionList!=null){
                                            for(int i = 0;i<pubMissionList.size();i++){
                                                missionList.add(pubMissionList.get(i));
                                            }
                                        }
                                        lastTime = missionList.get(missionList.size() - 1).getCreatedAt();
                                        MyLog.i("两个list的和----》"+missionList.size());
                                        dataListMsgBean = getBeanList(missionList);
                                        msgOrdersBeanAdapter.notifyDataSetChanged();
                                    }else if (actionType == STATE_NONE){//adapter为空时
                                        List<Mission> missionList = takeMissionList;
                                        if(pubMissionList!=null){
                                            for(int i = 0;i<pubMissionList.size();i++){
                                                missionList.add(pubMissionList.get(i));
                                            }
                                        }
                                        lastTime = missionList.get(missionList.size() - 1).getCreatedAt();
                                        MyLog.i("两个list的和----》"+missionList.size());
//                                        dataListMsgBean.clear();
                                        dataListMsgBean = getBeanList(missionList);
                                        msgOrdersBeanAdapter = new MsgOrdersBeanAdapter(getActivity(), dataListMsgBean);
                                        mRecyclerView.setAdapter(msgOrdersBeanAdapter);
                                    }//refresh
                                    MyLog.i("去掉对话框，去掉刷新");
                                    if(mDialog!=null){ mDialog.dismiss();}
                                    if (mSwipeRefreshLayout != null) {mSwipeRefreshLayout.setRefreshing(false);}
                              }//else加载数据
                            }else{
                                MyLog.e("查询订单消息失败"+e.getMessage());
                                if(mDialog!=null){ mDialog.dismiss();}
                                if (mSwipeRefreshLayout != null) {mSwipeRefreshLayout.setRefreshing(false);}
                            }//else
                        }//内部done
                    });
                }else{
                    MyLog.i("失败："+e.getMessage());
                }//else
            }//外部done
        });
    }//queryBmobData

    @Override
    public void onRefresh(){
        Toast.makeText(getActivity(), "更新啦啦啦", Toast.LENGTH_SHORT).show();
        queryBmobData(0, STATE_REFRESH);
    }

    private List<MsgOrdersBean>  getBeanList(List<Mission> list){
        MyLog.i("getBeanList里面");
        List<MsgOrdersBean> beanList = new ArrayList<MsgOrdersBean>();
        for(Mission mission:list){
            //一个mission 发布任务需要分为有三种分类，
            // 0.没人认领的，跳过封装；  mission.getClaimItemList()==null
            // 1.有认领人数的，没有确定认领人的；  mission.getChooseClaimant()==null(大多数) ,msgOrdersBean.getClaimFlag=1
            // 2.有确定认领人的，任务正在进行中的； mission.isFinished==false ,msgOrdersBean.getClaimFlag=2
            // 3.有确定认领人的，任务已经完成的；   mission.isFinished==true ,msgOrdersBean.getClaimFlag=3

            //第0种
            if(mission.getClaimItemList()==null){//若没有认领人，则跳过封装
                continue;
            }
            //第1种,有认领人数，没确定具体认领人
            if(mission.getChooseClaimant()==null){
                List<ClaimItems> claimItemsList =mission.getClaimItemList();
                String missionId = mission.getObjectId();
                int  takerNum = mission.getClaimItemList().size();
                String content=mission.getInfo();
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

//    private Boolean isSelected(List<ClaimItems> claimItemsList) {
//        for(int i=0;i<claimItemsList.size();i++){
//            if(claimItemsList.get(i).getClaimFlag()!=null){
//                claimPosition = i;
//                return true;
//            }
//        }
//        return false;
//    }

    private int isSelected2(JSONArray jsonArrayData){
        for(int i=0;i<jsonArrayData.length();i++){
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) jsonArrayData.get(i);
                MyLog.i("claimName-->"+jsonObject.optString("claimFlag"));
                if(jsonObject.optString("claimFlag")!= null){
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 此方法用来接收EventBus的数据，说明有人认领了任务，MissionTaker表发生了改变
     * 则要获取到改变的对象，并设置到消息Adapter上面
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OrdersMsgEvent2 event) {
        MyLog.i("有数据发生了改变------》");
        JSONObject jsonData = event.getJsonData();
        if(BmobUser.getCurrentUser(MyUser.class)==null){
            Toast.makeText(getContext(), "少侠，你还没有登陆呀~", Toast.LENGTH_SHORT).show();
            return;
        }
        MyLog.i("jsonData--->"+jsonData);
        MyLog.i("pubUserName-->"+jsonData.optString("pubUserName"));
        MyLog.i("missionId-->"+jsonData.optString("objectId"));
        MyLog.i("info-->"+jsonData.optString("info"));
        MyLog.i("time-->"+System.currentTimeMillis());

        JSONArray jsonArrayData = null;
        try {
            jsonArrayData = jsonData.getJSONArray("claimItemList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int selectFlag2 = isSelected2(jsonArrayData);

        if(selectFlag2 == -1){
            if(jsonData.optString("pubUserName").equals(myUser.getUsername())){
                MyLog.i("我是发布者");
                int num = jsonArrayData.length();
                String title = "你的任务被人认领啦~";
                String info = jsonData.optString("info");
                if(info.length()>10){
                    info = info.substring(0,5);
                }
                String content = jsonData.optString("info")+" 已被 "+num+" 微客认领，请注意选择微客";
                showNotification(title,content,"你的任务被其他微客认领了哦，快去看看吧！",1);
            }

        }else{
            try {
                JSONObject jsonObject = (JSONObject) jsonArrayData.get(selectFlag2);
                if(jsonObject.optString("claimFlag").equals("Doing")){
                    if(jsonObject.optString("claimName").equals(myUser.getUsername())) {
                        String title = "有微客选择你完成任务哦，快去看看吧！";
                        String pubUserName = jsonData.optString("pubUserName");
                        String content = pubUserName + "选择你完成任务" + "抓紧时间完成";
                        showNotification(title, content, title, 2);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(msgOrdersBeanAdapter==null)
            queryBmobData(0, STATE_NONE);
        else
            queryBmobData(0, STATE_REFRESH);
    }

    private void showNotification(String title,String content,String ticker,int notificationFlag){
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), OrdersFragment.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        Notification notify3 = new Notification.Builder(getActivity())
                .setSmallIcon(R.mipmap.witker_icon)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent3).setNumber(1).getNotification(); // 需要注意build()是在API
        // level16及之后增加的，API11可以使用getNotificatin()来替代
        notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        notificationManager.notify(notificationFlag, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提
    }

    private void cancelNotification(int notificationFlag){
        notificationManager.cancel(notificationFlag);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}//OrdersFrm_cls
