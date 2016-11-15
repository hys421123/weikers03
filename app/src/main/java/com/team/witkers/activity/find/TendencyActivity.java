package com.team.witkers.activity.find;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.TendencyAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendItems;
import com.team.witkers.eventbus.PubMissionEvent;
import com.team.witkers.eventbus.TendEvent;
import com.team.witkers.itemlistener.RecyclerItemClickListener;
import com.team.witkers.utils.BmobToast;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.NetworkUtils;
import com.team.witkers.views.StatusDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/8/9.
 */
public class TendencyActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        TendencyAdapter.onRecyclerViewItemClickListener, TendencyAdapter.onItemCommentClickListener,
        TendencyAdapter.onItemLikeClickListener,TendencyAdapter.onItemRlHeadClickListener{

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private TendencyAdapter mAdapter;
    private static final int QUERYLIMIT = 5;        // 每页的数据是10条
    private static final int STATE_FIRST = 0;// 第一次载入
    private static final int STATE_REFRESH = 1;// 下拉刷新
    private static final int STATE_MORE = 2;// 加载更多
    private static final int STATE_BACK = 3;// 从Details事件中撤回
    private static final int REQUESTCODE_TENDETAILS = 1;
    private static final int RESULT_BACK = 22;// Details返回事件
    private String lastTime = "";//记录一页展示的最后条目的时间，标记加载的更多页
    private String firstObjectId = "";//记录第一条目的id，标记是否重新刷新
    private int curPage = 0;        // 当前页的编号，从0开始
    private int lastVisibleItem;
    private List<TendItems> dataList = new ArrayList<>();

    private ProgressDialog mDialog;
//    private StatusDialog mDialog;
    private boolean isLike=false;
    private int currentItemPosition=0;
    private Set<Integer> treeSet;//建立TreeSet集合，存储改动的点赞条目
    private MyUser myUser;
    private BmobRelation relation ;
    private boolean isRefresh=false;//是否需要刷新

    @Override
    protected int setContentId() {
        return R.layout.activity_find_tendency;
    }

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_tendencies);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
//        ll_sendComment= (LinearLayout) findViewById(R.id.ll_sendComment);

    }

    @Override
    protected void initEventBus() {
        EventBus.getDefault().register(this);
    }

    /**
     * 更新数据
     *
     * @param event 事件
     */
    @Subscribe
    public void onEventMainThread(TendEvent event) {
        // 下拉刷新(从第一页开始装载数据)
//        MyToast.showToast(this, "更新啦");
        mDialog = new ProgressDialog(this, "正在加载");
        mDialog.show();
        queryBmobData(0, STATE_REFRESH);
    }

    @Override
    protected void setListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mAdapter == null) {
//                    MyLog.i("mAdapter_null");
                    return;
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
//                    MyToast.showToast(getActivity(), "Scroll to refresh");
                    queryBmobData(curPage, STATE_MORE);
                }//if
            }//onScrollStateChanged

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                MyLog.v("onScrolled ");
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                MyLog.v("lastCompletelyVisibleItem_"+lastVisibleItem);
//                MyLog.d("adapterItemCount_"+mAdapter.getItemCount());
            }
        });//setOnScrollListener

    }//setListener

    @Override
    protected void initData() {

        treeSet=new TreeSet<>();
        myUser= BmobUser.getCurrentUser(MyUser.class);
//        relation= new BmobRelation();


//        MyLog.v("initData and first");
        mDialog = new ProgressDialog(this, "正在加载");
        mDialog.show();
        queryBmobData(0, STATE_FIRST);
    }

    private void queryBmobData(int page, final int actionType) {
//        mDialog = new ProgressDialog(this, "正在加载");
////        mDialog = new StatusDialog(this, "正在加载");
//        mDialog.show();
        BmobQuery<TendItems> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        //返回5条数据，作为一页
        query.setLimit(QUERYLIMIT);


        if (actionType == STATE_REFRESH) {
            curPage = 0;
            dataList.clear();
        }
        if (actionType == STATE_MORE) {
            curPage++;
        }//STATE_MORE

        query.setSkip(curPage * QUERYLIMIT);// 忽略前n条数据


//    MyLog.v("query之前");
        //执行查询方法

        boolean isCache = query.hasCachedResult(TendItems.class);
//        MyLog.i("isCacheTendAct_2323223 "+isCache);
        switch (actionType){
            case STATE_FIRST:
                if(NetworkUtils.isNetWorkConnet(this))
                    query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
                else
                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
                break;
            case STATE_REFRESH:
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
                break;
            case STATE_MORE:
                if(isCache)
                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);
                break;
        }//switch

        query.include("pubUser");
        query.findObjects(new FindListener<TendItems>() {
            @Override
            public void done(List<TendItems> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        MyToast.showToast(TendencyActivity.this, "没有数据,快去发布吧!");
                    }//内if
                    // 将本次查询的数据添加到dataList中
                    for (TendItems tend : list) {
//                       isLikeList.add(tend.isLike());
//                        if(tend.getPubUser()==null){
//                            MyLog.e("get pubUser null_ ");
//                        }else{
//                            MyLog.v("pubUserObjId_ "+tend.getPubUser().getObjectId());
//                            MyLog.i("pubUserName11_ "+tend.getPubUser().getUsername());
//                        }

                        dataList.add(tend);
                    }//for
//                    MyLog.d("dataSize_ "+dataList.size());

//                            MyLog.v("query内，state first外");
                    if (actionType == STATE_FIRST) {
//                                MyLog.v("query内，state first内");
                        curPage = 0;
                        mAdapter = new TendencyAdapter(TendencyActivity.this, dataList);
                        mRecyclerView.setAdapter(mAdapter);
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        mAdapter.setOnItemClickListener(TendencyActivity.this);
                        mAdapter.setOnItemCommentClickListener(TendencyActivity.this);
                        mAdapter.setOnItemLikeClickListener(TendencyActivity.this);
                        mAdapter.setOnItemRlHeadClickListener(TendencyActivity.this);

                    } else {//内if
                        mAdapter.notifyDataSetChanged();
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                    }

                }//if e==null
                else {
                   MyLog.e("查询失败41");
//                    BmobToast.failureShow(TendencyActivity.this, "网络未连接", e.getMessage());
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }//else
                //下载完数据，停止刷新
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }//done
        });

        // 如果是加载更多


        //执行查询方法
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("动态");
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tendency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.menu_add_tend) {
            MyLog.v("add tend");
            Intent intent2 = new Intent(this, AddTendActivity.class);
            startActivity(intent2);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        isRefresh=true;
        uploadLikeItems();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_TENDETAILS&&resultCode==RESULT_BACK) {
//            MyLog.i("requestCode tendDetails启动返回结果");
//            queryBmobData(0, STATE_BACK);
//            MyLog.i("currentItemPosition_ "+currentItemPosition);
            if(data!=null) {
                TendItems tend2 = (TendItems) data.getSerializableExtra("ParentTend");
                if(dataList.get(currentItemPosition)!=tend2) {//若有修改，则更改dataList
                    dataList.set(currentItemPosition, tend2);
//                    mAdapter.notifyItemChanged(currentItemPosition);
                    mAdapter.notifyDataSetChanged();
                }
//                MyLog.i("likeNum in ActResult_ " + tend2.getLikeNum());
            }else
                MyLog.e("data_null");

//            mAdapter.notifyDataSetChanged();



        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View v, int itemPosition0) {
        Intent intent = new Intent(TendencyActivity.this, TendDetailsActivity.class);
        intent.putExtra("TendItems", dataList.get(itemPosition0));
        if(dataList.get(itemPosition0).getPubUser()==null)
            MyLog.e("pubUser in onItemClick_ null");
//        intent.putExtra("ItemPosition",itemPosition0);
        currentItemPosition=itemPosition0;
        startActivityForResult(intent, REQUESTCODE_TENDETAILS);

    }

    @Override
    public void onItemCommentClick(View v, TendItems tendTag) {
        MyLog.i("onItem comment");

        Intent intent = new Intent(TendencyActivity.this, TendDetailsActivity.class);
        intent.putExtra("TendItems", tendTag);
        intent.putExtra("IsItemComment", true);

        startActivityForResult(intent, REQUESTCODE_TENDETAILS);

    }

    @Override
    public void onItemLikeClick(View v,int itemPosition1) {

        treeSet.add(itemPosition1);
//        MyLog.d("TendencyActivity_itemPosition_ "+itemPosition1);
        TendItems tend=dataList.get(itemPosition1);
//        isLike=isLikeList.get(itemPosition1);
//        isLike=tend.isLike();
        if(tend.isLike()){//若为红色，点击即变为白色，点赞数减1，取消点赞
            tend.setLike(false);

            tend.setLikeNum(tend.getLikeNum()-1);
            //删除关联
            relation=new BmobRelation();
            relation.remove(tend);
            myUser.setLikeTendItems(relation);

        }else {// 点赞变红，增加点赞
            tend.setLike(true);

            tend.setLikeNum(tend.getLikeNum() + 1);

            relation=new BmobRelation();
            relation.add(tend);
//多对多关联指向`user`的`likeTendItems`字段
            myUser.setLikeTendItems(relation);

        }
//        tend.setLike(!isLike);
//        isLike=!isLike;
//        mAdapter.notifyItemChanged(itemPosition1);
        mAdapter.notifyDataSetChanged();
    }

//    //设置头像点击事件
//    @Override
//    public void onItemRoundHeadClickListener(View v, int position) {
//        MyLog.v("headClick");
////        TendItems item=dataList.get(position);
////       MyLog.i(item.getPubUser().getUsername());
//    }

    @Override
    public void onItemRlHeadClickListener(View v, int position) {
        MyLog.i("RlheadClick in TendencyAct");
        TendItems item=dataList.get(position);
        Intent intent2 = new Intent(this, PersonalHomePageActivity2.class);
        intent2.putExtra("fromTakeOutMissionAdapterTV",dataList.get(position).getPubUser());
        startActivity(intent2);
    }




    @Override
    protected void onPause() {
//        MyLog.i("TendAct onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        MyLog.i("onDestroy TendAct");

       uploadLikeItems();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void uploadLikeItems(){
        if(treeSet.size()!=0) {
            Iterator it = treeSet.iterator();
            //将需要更新的点赞对象放到新的集合中，批处理更新
            List<BmobObject> newDataList = new ArrayList<>();
            while (it.hasNext()) {
//            MyLog.i(""+it.next());
                newDataList.add(dataList.get((int) it.next()));
            }
            new BmobBatch().updateBatch(newDataList).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            BatchResult result = list.get(i);
                            BmobException ex = result.getError();
                            if (ex == null) {
//                            MyLog.d("第" + i + "个数据批量更新成功：" + result.getUpdatedAt());
                            } else {
                                MyLog.e("第" + i + "个数据批量更新失败：" + ex.getMessage() + "," + ex.getErrorCode());
                            }
                        }//for
                        if(isRefresh) {
                            MyLog.v("要刷新啦");
                            isRefresh=false;
                            queryBmobData(0, STATE_REFRESH);
                        }
                    } else
                        MyLog.e("批更新失败");
                }
            });//doBatch

            //只更新当前用户的关联关系
//            myUser.setValue("username",myUser.getUsername());
            myUser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        MyLog.i("用户关联关系更新成功");
                    }else
                        MyLog.e("用户关联关系更新失败");
                }
            });//update myUser
        }else {//if treeSet not 0
            if(isRefresh) {
                MyLog.v("要刷新啦");
                isRefresh=false;
                queryBmobData(0, STATE_REFRESH);
            }
        }
    }//uploadLikes



}//TendencyAct_cls
