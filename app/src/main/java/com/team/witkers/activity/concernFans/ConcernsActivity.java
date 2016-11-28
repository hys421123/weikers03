package com.team.witkers.activity.concernFans;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.ConcernsFansAdapter;
import com.team.witkers.adapter.MsgOrdersBeanAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ConcernBean;
import com.team.witkers.bean.ConcernFans;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.bean.MyUser;
import com.team.witkers.fragment.msgfrm.RecyclerViewAdapter;
import com.team.witkers.utils.NetworkUtils;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by lenovo on 2016/11/28.
 */

public class ConcernsActivity extends BaseActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener{

    private MyUser myUser;
    private ConcernsFansAdapter mAdapter;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private int mCount = 1;
    private List<ConcernFans> dataListConcernFans=new ArrayList<ConcernFans>();

    //把关注 列表取出来，作交集，得到 互关 粉丝
    private List<ConcernFans> concernsList=new ArrayList<>();

    private int lastVisibleItem;
    private ProgressDialog mDialog;
    private static final int STATE_FIRST = 0;// 第一次载入
    private static final int STATE_REFRESH = 1;// 下拉刷新
    private static final int STATE_MORE = 2;// 加载更多
    private static final int STATE_NONE = 3;//adapter为空时
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";   //最后一条的时间
    private int LIMIT = 6;        // 每页的数据是10条

    private  String userName,title;
    private boolean isFans;
    private Toolbar toolbar;
    private TextView tv_no;

    @Override
    protected int setContentId() {
        return R.layout.aall_pull_load;
    }

    @Override
    protected void getIntentData() {
        userName=getIntent().getStringExtra("userName1");
        title=getIntent().getStringExtra("title1");
        isFans=getIntent().getBooleanExtra("isFans",false);
    }

    @Override
    protected void initView() {
        mPullLoadMoreRecyclerView= (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setLinearLayout();
        mRecyclerViewAdapter = new RecyclerViewAdapter(this);
        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        //设置下拉刷新颜色
        mPullLoadMoreRecyclerView.setColorSchemeResources(R.color.swipe_refresh_color);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        tv_no= (TextView) findViewById(R.id.tv_no);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {
        mDialog = new ProgressDialog(this, "正在加载");
        mDialog.show();
        queryBmobData(0,STATE_FIRST);
    }

    private void queryBmobData(int page, final int actionType) {

        // 查询这个用户 关注的所有人
        BmobQuery<ConcernBean> query1 = new BmobQuery<ConcernBean>();
        query1.order("-createdAt");
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//                date = sdf.parse(lastTime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            query1.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));


            // 跳过之前页数并去掉重复数据
            query1.setSkip(page * LIMIT + 1);
        } else {
            page = 0;
            query1.setSkip(page);
        }

        // 设置每页数据个数
        query1.setLimit(LIMIT);
        //设置缓存机制，有网络时为网络优先，无网络时缓存优先
        boolean isCache = query1.hasCachedResult(ConcernBean.class);
        if(NetworkUtils.isNetWorkConnet(ConcernsActivity.this))
            query1.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        else
            query1.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);

        query1.addWhereEqualTo("name",userName);
        query1.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询关注者3成功");

                    ConcernBean concernBean=list.get(0);
                    if(isFans){
                        if(concernBean.getFansList()==null){
                            tv_no.setText("还没有任何粉丝");
                            tv_no.setVisibility(View.VISIBLE);
                            if(mDialog!=null){
                                mDialog.dismiss();
                            }
                            return;

                        }else {//粉丝中 有粉丝列表
                            dataListConcernFans = concernBean.getFansList();
                            concernsList=concernBean.getConcernsList();
                            if(concernsList!=null){
                                concernsList.retainAll(dataListConcernFans);
                                if(concernsList.size()!=0){// 粉丝、关注 两者有交集
                                    dataListConcernFans.removeAll(concernsList);//取 差集
                                    // 修改 交集的 isconcerned 属性
                                    for(ConcernFans fans:concernsList){
                                        fans.setConcerned(true);
                                    }
                                    //修改属性后，  并集
                                    dataListConcernFans.addAll(concernsList);
                                }//有交集
                            }//concernsList not null
                        }//粉丝中 有粉丝列表
                    }else{// isConcerns
                        if(concernBean.getConcernsList()==null) {
                            tv_no.setText("还没有关注任何人");
                            tv_no.setVisibility(View.VISIBLE);
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            return;
                        }else {//getConcernsList not null
                            dataListConcernFans=concernBean.getConcernsList();
                        }//getConcernsList not null
                    }//isConcerns

                       MyLog.v("concernsList_size_ "+concernBean.getConcernsList().size());
                        if(actionType==STATE_MORE){
                            mAdapter.notifyDataSetChanged();
                        }else{//下拉刷新或初始化
//                            lastTime=dataListConcernFans.get(dataListConcernFans.size()-1).
                              if(mAdapter==null){
                                  mAdapter=new ConcernsFansAdapter(ConcernsActivity.this,dataListConcernFans);
                                  mPullLoadMoreRecyclerView.setAdapter(mAdapter);
                              }else{
                                  mAdapter.notifyDataSetChanged();
                              }//mAdapter not null

                        }//下拉刷新或初始化

                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                   if(mDialog!=null){
                        mDialog.dismiss();
                    }
                }//e==null
                 else{//e not null
                    MyLog.i("查询关注者3失败");
                    if(mDialog!=null){
                        mDialog.dismiss();
                    }
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                }//else
            }//done
        });
    }//queryBmobData
    @Override
    public void onRefresh() {
        MyLog.v("onRefresh");
        queryBmobData(0, STATE_REFRESH);
    }

    @Override
    public void onLoadMore() {
        MyLog.v("onLoadMore");
        queryBmobData(0, STATE_MORE);
    }
}//ConcernsAct_cls
