package com.team.witkers.activity.homeitem;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.find.TendDetailsActivity;
import com.team.witkers.adapter.HeaderAndFooterWrapperPersonalPage;
import com.team.witkers.adapter.TendencyAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendItems;
import com.team.witkers.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/8/23.
 */
public class PersonalHomePageActivity2 extends BaseActivity implements
        TendencyAdapter.onRecyclerViewItemClickListener,
        TendencyAdapter.onItemCommentClickListener,
        TendencyAdapter.onItemLikeClickListener{

    private Toolbar toolbar;
    private MyUser person;
    private RecyclerView mRecyclerView;
    private View header,footer;
    private LinearLayoutManager linearLayoutManager;
    private TendencyAdapter mAdapter;
    private HeaderAndFooterWrapperPersonalPage mHeaderAndFooterWrapper;
    private List<TendItems> dataList;
    private ProgressDialog mDialog;
    private static final int REQUESTCODE_TENDETAILS = 1;
    private static final int RESULT_BACK = 22;// Details返回事件
    private int currentItemPosition=0;
    private Set<Integer> treeSet;//建立TreeSet集合，存储改动的点赞条目
    private BmobRelation relation ;
    private MyUser myUser;
    private int pubMissionNum,concernNum,fansNum;
    private Boolean isConcerned = false;

    @Override
    protected int setContentId() {
        return R.layout.activity_personal_home_page2;
    }

    @Override
    protected void initView() {

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView= (RecyclerView) findViewById(R.id.mRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }



    @Override
    protected void initToolBar() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle(person.getUsername());
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

    private Boolean getPubMissionNum(final Boolean isNothing){
        //查询这个用户发布过的所有任务
        BmobQuery<Mission> query = new BmobQuery<Mission>();
        query.addWhereEqualTo("pubUserName",person.getUsername());
        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> list, BmobException e) {
                MyLog.i("查询任务数成功"+list.size());
                pubMissionNum = list.size();
                getConcernNum(isNothing);

            }
        });
        return isNothing;
    }

    private Boolean getConcernNum(final Boolean isNothing){
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereRelatedTo("concernPerson", new BmobPointer(person));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                MyLog.i("查询关注数成功"+list.size());
                concernNum = list.size();
                getFansNum(isNothing);
            }
        });
        return isNothing;
    }
    private void getFansNum(final Boolean isNothing){
        MyLog.i("开始查询fans数");
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereRelatedTo("fansPerson", new BmobPointer(person));
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                MyLog.i("查询粉丝数成功"+list.size());
                fansNum = list.size();
                if(list.size()==0){
                    isConcerned = false;
                }else{
                    for(int i=0;i<list.size();i++){
                        //我是别人的fans，说明我关注了他
                        if(list.get(i).getUsername().equals(myUser.getUsername())){
                            isConcerned = true;
                        }
                    }
                }
                MyLog.i(pubMissionNum+"---"+concernNum+"----"+fansNum+"---"+isConcerned);
                setHeadAndFootViewWrapper(isNothing);
            }
        });
    }


    @Override
    protected void getIntentData() {
        person = (MyUser) this.getIntent().getSerializableExtra("fromTakeOutMissionAdapterTV");
//        userName = this.getIntent().getStringExtra("fromTakeOutMissionAdapterTV");
        MyLog.i("name ---->"+person.getUsername());
//       String userName = this.getIntent().getStringExtra("fromOrdersShowAdapter");
//        MyLog.i("userName-->"+userName);
//        queryUser(userName);
    }


    private void queryUser(String userName){
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e==null){
                    MyUser myUser = list.get(0);
                    PersonalHomePageActivity2.this.person = myUser;
                }else{
                    MyLog.i("erro");
                }
            }
        });
    }

    @Override
    protected void initData() {
        treeSet=new TreeSet<>();
        dataList=new ArrayList<>();
        myUser= BmobUser.getCurrentUser(MyUser.class);

        mDialog=new ProgressDialog(this,"正在加载..");
        mDialog.show();

        BmobQuery<TendItems> query = new BmobQuery<TendItems>();
        MyLog.i("name_ "+person.getUsername());
        query.addWhereEqualTo("friendName", person.getUsername());
//        query.order("createdAt");
        // 按事件降序排序
        query.order("-createdAt");

        boolean isCache = query.hasCachedResult(TendItems.class);
        MyLog.i("isCachePersonPage "+isCache);
        if(NetworkUtils.isNetWorkConnet(this))
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);
        else
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);

        query.findObjects(new FindListener<TendItems>() {
            @Override
            public void done(List<TendItems> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询成功");
                    //mAdapter属于 下面的正文主体,即发布人的动态项
                    mAdapter=new TendencyAdapter(PersonalHomePageActivity2.this, list);
                    boolean isNothing=false;
                    if(list.size()==0)
                        isNothing=true;
                    else{//动态item不为零时才有必要添加监听事件
                        dataList=list;
                        mAdapter.setOnItemClickListener(PersonalHomePageActivity2.this);
                        mAdapter.setOnItemCommentClickListener(PersonalHomePageActivity2.this);
                        mAdapter.setOnItemLikeClickListener(PersonalHomePageActivity2.this);
                    }
//                    setHeadAndFootViewWrapper(isNothing);
                    getPubMissionNum(isNothing);

                }else {
                    MyLog.e("查询失败");
                    mAdapter=new TendencyAdapter(PersonalHomePageActivity2.this, new ArrayList<TendItems>());
                    setHeadAndFootViewWrapper(false);
                    mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
                    if(mDialog!=null)
                        mDialog.dismiss();
                }
            }//done
        });
    }

    private void setHeadAndFootViewWrapper(boolean isNothing){
        //添加头部，尾部
        MyLog.i(pubMissionNum+"---"+concernNum+"----"+fansNum+"---"+isConcerned);
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapperPersonalPage(this,person,mAdapter,pubMissionNum,concernNum,fansNum,isConcerned);
        header = LayoutInflater.from(this).inflate(R.layout.recyclerview_personpage_heads,mRecyclerView, false);
        if(isNothing)
            footer=LayoutInflater.from(this).inflate(R.layout.recyclerview_personpage_nothing,mRecyclerView, false);
        else
            footer=LayoutInflater.from(this).inflate(R.layout.recyclerview_comment_footitems,mRecyclerView, false);
        mHeaderAndFooterWrapper.addFootView(footer);
        mHeaderAndFooterWrapper.addHeaderView(header);
        //设置适配器
        if(mDialog!=null)
            mDialog.dismiss();
        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
    }//setHeadAndFootViewWrapper

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_TENDETAILS&&resultCode==RESULT_BACK) {

//            MyLog.i("currentItemPosition_ "+currentItemPosition);
            if(data!=null) {
                TendItems tend2 = (TendItems) data.getSerializableExtra("ParentTend");
                if(dataList.get(currentItemPosition)!=tend2) {//若有修改，则更改dataList
                    dataList.set(currentItemPosition, tend2);
//                    mHeaderAndFooterWrapper.notifyDataSetChanged();
                    mHeaderAndFooterWrapper.notifyItemChanged(currentItemPosition+1);
                }
//                MyLog.i("likeNum in ActResult_ " + tend2.getLikeNum());
            }else
                MyLog.e("data_null");
        }//if
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(this, TendDetailsActivity.class);
        intent.putExtra("TendItems", dataList.get(position));
        currentItemPosition=position;
        startActivityForResult(intent, REQUESTCODE_TENDETAILS);
    }

    @Override
    public void onItemCommentClick(View v, TendItems tendTag) {
        Intent intent = new Intent(this, TendDetailsActivity.class);
        intent.putExtra("TendItems", tendTag);
        intent.putExtra("IsItemComment", true);
        startActivityForResult(intent, REQUESTCODE_TENDETAILS);
    }

    @Override
    public void onItemLikeClick(View v, int itemPosition) {
        treeSet.add(itemPosition);
//        MyLog.d("TendencyActivity_itemPosition_ "+itemPosition1);
        TendItems tend=dataList.get(itemPosition);

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
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }//onItemLike
    @Override
    protected void onDestroy() {
        MyLog.i("onDestroy PersonPageAct");
        uploadLikeItems();
        super.onDestroy();
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
//                        if(isRefresh) {
//                            MyLog.v("要刷新啦");
//                            isRefresh=false;
//                            queryBmobData(0, STATE_REFRESH);
//                        }
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
//            if(isRefresh) {
//                MyLog.v("要刷新啦");
//                isRefresh=false;
//                queryBmobData(0, STATE_REFRESH);
//            }
        }
    }//uploadLikes


}//PersonHomeActivity
