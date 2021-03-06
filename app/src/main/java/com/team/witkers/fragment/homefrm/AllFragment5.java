package com.team.witkers.fragment.homefrm;

/**
 * Created by jin on 2016/8/18.
 */
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.MissionAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.Mission;
import com.team.witkers.eventbus.MsgEvent;
import com.team.witkers.eventbus.OrdersMsgEvent2;
import com.team.witkers.eventbus.PubMissionEvent;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.NetworkUtils;
import com.team.witkers.views.ProgressWheel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by hys on 2016/7/30.
 */

public class AllFragment5 extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private ProgressWheel progressWheel;

    private TextView tv_takeoutfrm_empty,tv_no;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private MissionAdapter mAdapter;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private static final int STATE_NONE = 2;// 第一次没有加载到数据时
    private static final int REQUEST_CODE =1;//响应码
    private List<Mission> dataList = new ArrayList<>();
    private int LIMIT = 8;        // 每页的数据是8条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime = "";
    private int lastVisibleItem;

//    114.615562,30.518573(创业街)
//      114.414878,30.515563(华中科技大学)
//    114.399193,30.506615（光谷广场）
//    114.384044,30.508667（保利华都）
    BmobGeoPoint point = new BmobGeoPoint(114.398331,30.506929);

    BmobGeoPoint myPoint;
    @Override
    protected int setContentId() {
        return R.layout.fragment_all_2;
    }


    @Override
    protected void initDataBeforeView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView(View view) {
        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_wheel);
        progressWheel.setBarColor(Color.rgb(162,180,186));

        tv_takeoutfrm_empty= (TextView) view.findViewById(R.id.tv_takeoutfrm_empty);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefreshLayout);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.rv_takeoutfrm);
        tv_no= (TextView) view.findViewById(R.id.tv_no);

        linearLayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(90,173,241));
    }

    @Override
    protected void loadDataAfterView() {


        myPoint = (BmobGeoPoint) getArguments().getSerializable("myPoint");

        if(myPoint!=null) {
            double lat = myPoint.getLatitude();
            double lon = myPoint.getLongitude();
            MyLog.d("myPoint_ " + lat + "// " + lon);
        }else
            MyLog.e("myPoint null");

//        final ProgressDialog mDialog = new ProgressDialog(getActivity(), "正在加载");
//        mDialog.show();

        BmobQuery<Mission> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        //返回8条数据，
        query.setLimit(LIMIT);

//        查询附近的人
        query.addWhereNear("missionLocation",myPoint);
//        指定距离范围2千米
        query.addWhereWithinKilometers("missionLocation",myPoint,2.0);

//        执行查询方法
        boolean isCache = query.hasCachedResult(Mission.class);
        if(isCache){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        query.include("pubUser");
        // 已经选择微客的任务 去掉， chooseClaimant==null 即还没选择微客
//        query.addWhereEqualTo("chooseClaimant",null);   无效啊
        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> object, BmobException e) {
                if(e==null){
//                    MyLog.v("chooseClaimant_ null");
                    if (object.size() == 0) {
                        MyToast.showToast(getActivity(),"没有数据,快去发布吧!");
//                        if (mDialog != null) {
//                            mDialog.dismiss();
//                        }
                        progressWheel.stopSpinning();
                        tv_no.setVisibility(View.VISIBLE);
                        return;
                    }

                    // TODO Auto-generated method stub
                    lastTime = object.get(object.size() - 1).getCreatedAt();


                    // Log.d("lastTime", lastTime);
                    // 将本次查询的数据添加到lostList中
                    for (Mission lb : object) {
//                        MyLog.i("pubUsername_ "+lb.getPubUser().getUsername());
                        if(lb.getChooseClaimant()==null)
                           dataList.add(lb);
                    }
                    mAdapter = new MissionAdapter(getActivity(),dataList);
                    mRecyclerView.setAdapter(mAdapter );
//                    if (mDialog != null) {
//                        mDialog.dismiss();
//                        progressWheel.stopSpinning();
//                    }
                    progressWheel.stopSpinning();
                }else{
                   MyLog.e("查询失败_ " + e.getMessage());
//                    if (mDialog != null) {
//                        mDialog.dismiss();
//                        progressWheel.stopSpinning();
//                    }
                    progressWheel.stopSpinning();
                }//else
            }//done
        });
    }

    @Override
    protected void setListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                && lastVisibleItem + 1 ==adapter.getItemCount()

                if(mAdapter==null){
                    MyLog.i("mAdapter_null");

                    queryData(0, STATE_NONE);
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==mAdapter.getItemCount()){
                    MyToast.showToast(getActivity(), "下拉刷新");
                    queryData(curPage, STATE_MORE);
                }//if
            }//onScrollStateChanged

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //一看到最后可见项就刷新
                lastVisibleItem =linearLayoutManager.findLastVisibleItemPosition();
//                MyLog.v("lastCompletelyVisibleItem_"+lastVisibleItem);
//                MyLog.d("adapterItemCount_"+mAdapter.getItemCount());
            }
        });//setScrollListener
    }//setListener

    @Override
    public void onRefresh() {
        if(mAdapter==null)
            queryData(0, STATE_NONE);
        else
            queryData(0, STATE_REFRESH);
//        MyToast.showToast(getActivity(),"onRefresh");
    }

    /**
     * 分页获取数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryData(int page, final int actionType) {
        BmobQuery<Mission> query = new BmobQuery<>();

//        查询附近的人
        query.addWhereNear("missionLocation",myPoint);
//        指定距离范围2千米
        query.addWhereWithinKilometers("missionLocation",myPoint,2.0);
//        执行查询方法

        // 按时间降序查询
        query.order("-createdAt");
//        query.include("myUser");
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 跳过之前页数并去掉重复数据
            query.setSkip(page * LIMIT + 1);
//            MyLog.i("skip");
        } else {
            page = 0;
            query.setSkip(page);
        }
        // 设置每页数据个数
        query.setLimit(LIMIT);
        // 查找数据

      boolean isCache = query.hasCachedResult(Mission.class);
//        MyLog.i("isCacheAllFrm111 "+isCache);
        if(NetworkUtils.isNetWorkConnet(getActivity()))
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        else
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);

        query.include("pubUser");
        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> list, BmobException e) {
                if(e==null) {//成功查找数据
                    MyLog.i("成功查询数据");
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                            dataList.clear();
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }//下拉刷新操作

                        // 将本次查询的数据添加到bankCards中
                        for (Mission lb : list) {
//                      MyLog.i("pubUsername_ "+lb.getPubUser().getUsername());
                            if(lb.getChooseClaimant()==null)
                                dataList.add(lb);
                        }

//                        MyLog.e("mAdapter null2");
                        if(mAdapter==null){
                            mAdapter=new MissionAdapter(getActivity(),dataList);
                            mRecyclerView.setAdapter(mAdapter );
                        }else
                            mAdapter.notifyDataSetChanged();

                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作  curPage了
                        curPage++;

                    } //if数据不为空完毕

                    else if (actionType == STATE_MORE) {
                        MyToast.showToast(getActivity(),"没有更多数据了");
                    } else if (actionType == STATE_REFRESH) {
                        MyToast.showToast(getActivity(),"还没有数据,快去发布吧！");
                    }
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }else{//查找数据失败
                    MyToast.showToast(getActivity(),"数据加载失败");
                    MyLog.e("查询失败"+e.getMessage());
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }//else

            }//done
        });//findObjects


    }
    /**
     * 更新数据
     *
     * @param event 事件
     */
    @Subscribe
    public void onEventMainThread(PubMissionEvent event) {
        // 下拉刷新(从第一页开始装载数据)
        MyToast.showToast(getActivity(),"更新啦");
//        MyLog.i("onEventMain该更新了");
        if(mAdapter==null)
            queryData(0, STATE_NONE);
        else
            queryData(0, STATE_REFRESH);
    }

    @Subscribe
    public void onEventMainThread(OrdersMsgEvent2 event) {
        // 下拉刷新(从第一页开始装载数据)
//        MyToast.showToast(getActivity(),"有人发布任务啦啦");
        MyLog.i("有人发布新任务啦");
        if(mAdapter==null)
            queryData(0, STATE_NONE);
        else
            queryData(0, STATE_REFRESH);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MyLog.v("AllFrm destroy");
        EventBus.getDefault().unregister(this);
    }




//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.i("Test","position-->"+position);
//    }
}//Frm00_cls
