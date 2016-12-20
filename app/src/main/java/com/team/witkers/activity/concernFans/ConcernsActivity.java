package com.team.witkers.activity.concernFans;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
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
    private boolean isOthers;//是否 建立的是别人的关注、粉丝
    private Toolbar toolbar;
    private TextView tv_no;
    // 我（已登录用户）自己关注 的 用户
    private List<ConcernFans> meConcernsList=null;

    @Override
    protected int setContentId() {
        return R.layout.aall_pull_load;
    }

    @Override
    protected void getIntentData() {
        userName=getIntent().getStringExtra("userName1");
        title=getIntent().getStringExtra("title1");
        String str=title.substring(0,1);
//        MyLog.v(str);
        isOthers=getIntent().getBooleanExtra("isOthers",false);
        isFans=getIntent().getBooleanExtra("isFans",false);
    }

    @Override
    protected void initView() {
        mPullLoadMoreRecyclerView= (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setLinearLayout();
        mRecyclerViewAdapter = new RecyclerViewAdapter(this);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
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

        if(isOthers){
            MyLog.v("处理别人的粉丝 关注");
            queryMeData(0,STATE_FIRST);//有关注别人
        }
        else{
            MyLog.v("处理自己的粉丝 关注");
            queryBmobData(0,STATE_FIRST);
        }

    }//initData

    // 查询 自己的数据，自己关注的用户
    private void queryMeData(final int page, final int state){
        BmobQuery<ConcernBean> query0 = new BmobQuery<ConcernBean>();
        query0.addWhereEqualTo("name", MyApplication.mUser.getUsername());
        query0.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询我的关注成功");
                    ConcernBean concernBean0=list.get(0);
                    meConcernsList= concernBean0.getConcernsList();
                    queryBmobData(page,state);
                }else{
                    MyLog.i("查询我的关注失败");
                }

            }//done
        });//findObjs

    }//queryMeData

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
                    String no="";
                    if(isFans){// 如果选择粉丝选项
                        if(concernBean.getFansList()==null||concernBean.getFansList().size()==0){
                            tv_no.setText("还没有任何粉丝");
                            tv_no.setVisibility(View.VISIBLE);
                            if(mDialog!=null){
                                mDialog.dismiss();
                            }
                            return;
                        }else{//getFansList.size() !=0
                            dataListConcernFans=concernBean.getFansList();
                            MyLog.d("还有粉丝的");
                        }
                    }else {// 选择有粉丝  isFans
                        if(concernBean.getConcernsList()==null||concernBean.getConcernsList().size()==0){
                            tv_no.setText("还没有关注任何人");
                            tv_no.setVisibility(View.VISIBLE);
                            if(mDialog!=null){
                                mDialog.dismiss();
                            }
                            return;
                        }else{//getFansList.size() !=0
                            if(!isOthers)// 处理自己的粉丝时，才要获取两次getConcernList()
                                meConcernsList=concernBean.getConcernsList();
                            dataListConcernFans=concernBean.getConcernsList();
                        }
                    }// 选择 关注者

                        if(actionType==STATE_MORE){
                            mAdapter.notifyDataSetChanged();
                        }else{//下拉刷新或初始化
//                            lastTime=dataListConcernFans.get(dataListConcernFans.size()-1).
                              if(mAdapter==null){
                                  mAdapter=new ConcernsFansAdapter(ConcernsActivity.this,dataListConcernFans,meConcernsList);
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
