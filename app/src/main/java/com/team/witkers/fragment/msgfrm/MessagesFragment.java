package com.team.witkers.fragment.msgfrm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.find.TendDetailsActivity;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.adapter.MsgTendAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.MsgTendBean;
import com.team.witkers.bean.TendComments;
import com.team.witkers.bean.TendItems;
import com.team.witkers.eventbus.MonitorEvent;
import com.team.witkers.eventbus.MsgEvent;
import com.team.witkers.utils.MyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by shanyao on 2016/6/17.
 */
public class MessagesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,MsgTendAdapter.onRecyclerViewItemClickListener {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private List<TendItems> dataList = new ArrayList<>();
    private MsgTendAdapter mAdapter;
    private List<MsgTendBean> msgList=new ArrayList<>();
    private static final int QUERYLIMIT = 8;        // 每页的数据是7条
    private List<Integer> list=new ArrayList<>();
    private ProgressDialog mDialog;
    private static final int STATE_FIRST = 0;// 第一次载入
    private static final int STATE_REFRESH = 1;// 下拉刷新
    private static final int STATE_MORE = 2;// 加载更多
    private static final int STATE_NONE = 3;//adapter为空时
    private int curPage = 0;        // 当前页的编号，从0开始
    private int lastVisibleItem;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MyLog.i("msgFrm create ");
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        MyLog.i("msgFrm onattach ");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MsgFrm create");
    }

    @Override
    public void onResume() {

        super.onResume();
        if(MyApplication.mUser==null){
            MyToast.showToast(getActivity(),"亲，请先登录好吗");

            if(mAdapter!=null){
                msgList.clear();
                mAdapter.notifyDataSetChanged();
            }
            return;
        }
        MyLog.i("MsgFrm onResume");

    }

    @Override
    protected int setContentId() {
        return R.layout.fragment_message_msg;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView= (RecyclerView) view.findViewById(R.id.mRecyclerView);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.msgSwipeRefreshLayout);
//        Button btn= (Button) view.findViewById(R.id.btn_add);
    }

    @Override
    protected void initDataBeforeView() {
        EventBus.getDefault().register(this);
    }


    @Override
    protected void loadDataAfterView() {

        msgList=new ArrayList<>();
//        MyLog.v("set Adapter");
//        msgList.add(msg3);
//        msgList.add(msg4);
        if(MyApplication.mUser==null){
            MyToast.showToast(getActivity(),"亲，请先登录好吗");
            return;
        }

        mDialog = new ProgressDialog(getActivity(), "正在加载");
        mDialog.show();
//        loadMsgList();
        queryBmobData(0,STATE_FIRST);
    }

    @Override
    protected void setListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);

                MyLog.i("onScrollStateChanged");
//                if (mAdapter == null) {
////                    MyLog.i("mAdapter_null");
//                    queryBmobData(0, STATE_REFRESH);
//                    return;
//                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    queryBmobData(0, STATE_REFRESH);
                    return;
                }

//                switch (newState) {
//                    case  RecyclerView.SCROLL_STATE_IDLE:
//                        MyLog.i("idle");
//                        //滚动停止
//                        break;
//                    case  RecyclerView.SCROLL_STATE_DRAGGING:
//                        MyLog.i("dragging");
//                        //正在滚动
//                        break;
//                    case  RecyclerView.SCROLL_STATE_SETTLING:
//                        MyLog.i("setting");
//                        //手指用力滑动,离开ListView后,由于惯性继续滚动
//                        break;
//                }
                
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
//                    MyToast.showToast(getActivity(), "Scroll to refresh");
                    MyLog.i("state more!!");
                    queryBmobData(curPage, STATE_MORE);
                }//if
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }



    private void queryBmobData(int page, final int actionType){
        if(MyApplication.mUser==null){
            MyToast.showToast(getActivity(),"亲，请先登录好吗");
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if(mAdapter!=null){
                msgList.clear();
                mAdapter.notifyDataSetChanged();
            }
            return;
        }

        BmobQuery<TendComments> query2 = new BmobQuery<>();
//        MyLog.i("myUser_name_ "+ MyApplication.mUser.getUsername());
        query2.addWhereEqualTo("tendUser", MyApplication.mUser);
        query2.order("-createdAt");
        query2.setLimit(QUERYLIMIT);

            MyLog.v("queryBmob");
        if (actionType == STATE_MORE) {
//            MyLog.v("state more222");
            curPage++;
            query2.setSkip(curPage * QUERYLIMIT);// 忽略前n条数据
        }//STATE_MORE
        if (actionType == STATE_REFRESH) {
            curPage = 0;
            msgList.clear();
        }
        //跳过自己评论的
        query2.addWhereNotEqualTo("commentUserName", MyApplication.mUser.getUsername());
        query2.include("tendItems");
        query2.findObjects(new FindListener<TendComments>() {
            @Override
            public void done(List<TendComments> list, BmobException e) {
                if(e==null){
                    if (list.size() == 0) {
//                        MyLog.i("没有动态的数据啊");
//                        MyToast.showToast(getActivity(), "亲，已经到底了!");
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        return;
                    }//内if

                    MyLog.i("查询回复消息成功list_size_ "+list.size());
                    MyLog.v("curpage_ "+curPage);
                    for(int i=0;i<list.size();i++) {
                        MsgTendBean msg2 = new MsgTendBean();
                        TendItems tend1 = list.get(i).getTendItems();
//                        if(tend1==null){
//                            MyLog.e("tend_null");
//                        }else{
//                            MyLog.i("tendContent_ "+tend1.getContent());
//                        }

                        TendComments comment = list.get(i);

                        msg2.setTend(tend1);
                        msg2.setCommentHeadUrl(comment.getCommentUserHead());
                        msg2.setCommentUserName(comment.getCommentUserName());
                        msg2.setCommentContent(comment.getCommentContent());
                        msg2.setCommentTime(comment.getCommentPubtime());
                        msgList.add(msg2);
                    }
                   if(actionType==STATE_FIRST){

                       MyLog.i("first_");
                       curPage = 0;
                       mAdapter=new MsgTendAdapter(getActivity(),msgList);
                       mRecyclerView.setAdapter(mAdapter);
                       mAdapter.setOnItemClickListener(MessagesFragment.this);
                   }
                    if(actionType==STATE_REFRESH){
                        if(mAdapter==null){
                            mAdapter=new MsgTendAdapter(getActivity(),msgList);
                            mRecyclerView.setAdapter(mAdapter );
                        }else
                            mAdapter.notifyDataSetChanged();

                        //下载完数据，停止刷新
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    
                    if(actionType==STATE_MORE)
                        mAdapter.notifyDataSetChanged();
                }else{
                    MyLog.e("查询回复消息失败"+e.getMessage());
                    mDialog.dismiss();

                    }//else
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }//done
        });//findObjs

    }
    /**
     * 更新数据
     *
     * @param event 事件
     */
    @Subscribe
    public void onEventMainThread(MonitorEvent event) {
        // 下拉刷新(从第一页开始装载数据)
//        MyToast.showToast(getActivity(),"更新啦");

        MyLog.i("MsgFrm_onEventMain更新了");
        MsgTendBean msg2=new MsgTendBean();
        TendItems tend1=event.getTend();
        TendComments comment=event.getComment();
        
        if(tend1==null)
            MyLog.e("tend1_null");
        //取出最新的评论装填进去
//       TendComments comment= tend1.getCommentsList().get(tend1.getCommentsList().size()-1);
//        TendComments comment=new TendComments();//替补
        msg2.setTend(tend1);
        msg2.setCommentHeadUrl(comment.getCommentUserHead());
        msg2.setCommentUserName(comment.getCommentUserName());
        msg2.setCommentContent(comment.getCommentContent());
        msg2.setCommentTime(comment.getCommentPubtime());

        MyLog.v("msg2_commentUserName_ "+comment.getCommentUserName());
//        msgList.add(msg2);
        msgList.add(0,msg2);
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEventMainThread(MsgEvent event) {

//        MyLog.i("msgEvent 接收事件");
        if(event.isClearMsg()) {
            msgList.clear();
            mAdapter.notifyDataSetChanged();
        }

        List<TendComments> msgList2=event.getCommentsList();
        for(TendComments comment:msgList2){
            MsgTendBean msg2=new MsgTendBean();
            msg2.setTend(comment.getTendItems());
            msg2.setCommentHeadUrl(comment.getCommentUserHead());
            msg2.setCommentUserName(comment.getCommentUserName());
            msg2.setCommentContent(comment.getCommentContent());
            msg2.setCommentTime(comment.getCommentPubtime());
            msgList.add(msg2);
        }
        mAdapter.notifyDataSetChanged();


    }


    @Override
    public void onDestroy() {
        MyLog.e("MsgFrm destroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        MyLog.i("onRefresh");
        queryBmobData(0,STATE_REFRESH);
    }

    @Override
    public void onItemClick(View v, TendItems tendItem) {
        MyLog.d("onItemClick");
        Intent intent = new Intent(getActivity(), TendDetailsActivity.class);
        intent.putExtra("TendItems", tendItem);
        MyLog.i("tendTime_ "+tendItem.getCreatedAt());
//        if(tendItem==null)
//            MyLog.e("tendItem_null");
//        else
//             MyLog.v("tendItem_content_ "+tendItem.getContent());
//        intent.putExtra("ItemPosition",itemPosition0);
//        startActivityForResult(intent, REQUESTCODE_TENDETAILS);
        startActivity(intent);
//        MyLog.v("tendContent_ "+tendItem.getContent());
    }

//    public void addClick(View v ){
//        MsgTendBean msg3=new MsgTendBean();
//        //取出最新的评论装填进去
//
//        msg3.setTend(null);
//        msg3.setCommentHeadUrl("");
//        msg3.setCommentUserName("iii2");
//        msg3.setCommentContent("天啦");
//        msg3.setCommentTime("几点前");

//        MyLog.v("增加");
//        msgList.add(msg3);
//        mAdapter.notifyDataSetChanged();
//    }

}
