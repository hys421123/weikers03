package com.team.witkers.activity.find;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.HeaderAndFooterWrapperTendDetails;
import com.team.witkers.adapter.TendCommentAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendComments;
import com.team.witkers.bean.TendItems;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.TimeUtils2;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/8/15.
 */
public class TendDetailsActivity extends BaseActivity implements View.OnClickListener, HeaderAndFooterWrapperTendDetails.onItemCommentClickListener, HeaderAndFooterWrapperTendDetails.onItemLikeClickListener {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private View ll_sendComment;
    //    private TendCommentAdapter mAdapter;
    private TendCommentAdapter mAdapter;
    private HeaderAndFooterWrapperTendDetails mHeaderAndFooterWrapper;

    private EditText et_comment;
    private ImageButton btn_send;

    private boolean isItemComment;//是否由 评论按钮点击进入
    private TendItems parentTend;
    private List<TendComments> dataList;
    private View header;
    private TextView tvCommentNum;
    private boolean isLike0ld;
    private int itemPosition;
    private static final int RESULT_BACK = 22;// 返回事件
    private MyUser myUser;
    private BmobRelation relation;

    @Override
    protected int setContentId() {
        return R.layout.activity_find_tenddetails;
    }

    @Override
    protected void initView() {
//        MyLog.i("Details initView");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ll_sendComment = findViewById(R.id.ll_sendComment);
        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_send = (ImageButton) findViewById(R.id.btn_send);

        if (isItemComment) {
//            ll_sendComment.setVisibility(View.VISIBLE);
            //强制获取焦点，显示键盘
            et_comment.setFocusable(true);
            et_comment.requestFocus();
        } else {
//            ll_sendComment.setVisibility();
        }

//        et_comment.setFocusable(true);
//        //控制recyclerView的间隙
//        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recyclerview_space);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

//        iv_repost = (ImageButton) findViewById(R.id.ib_repost);
//        iv_comment = (ImageView) findViewById(R.id.iv_comment);
//        iv_like = (ImageView) findViewById(R.id.iv_like);
//
//        ll_topview = (LinearLayout) findViewById(R.id.ll_topview);
//        ll_repost = (LinearLayout) findViewById(R.id.ll_repost);
//        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
//        ll_like = (LinearLayout) findViewById(R.id.ll_like);
//        roundIv_tendency_head = (RoundedImageView) findViewById(R.id.roundIv_tendency_head);
//        tv_tendName = (TextView) findViewById(R.id.tv_tendName);
//        tv_tendPubtime = (TextView) findViewById(R.id.tv_tendPubtime);
//        tv_commentNum = (TextView) findViewById(R.id.tv_commentNum);
//        tv_likeNum = (TextView) findViewById(R.id.tv_likeNum);
//        tv_tendPubcontent = (TextView) findViewById(R.id.tv_tendPubcontent);
//        ninegrid = (NineGridImageView) findViewById(R.id.ninegrid);
//        ninegrid.setAdapter(mAdapterNine);
    }

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_comment_headitems, view, false);
//        mAdapter.setHeaderView(header);
    }

    @Override
    protected void setListener() {
        btn_send.setOnClickListener(this);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("动态详情");
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if (item.getItemId() == android.R.id.home) {
            Intent intent2 = new Intent();
            intent2.putExtra("ParentTend", parentTend);
            setResult(RESULT_BACK, intent2);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tendetails, menu);
        return true;
    }

    @Override
    protected void getIntentData() {
//        MyLog.i("getIntentData ");
        parentTend = (TendItems) getIntent().getSerializableExtra("TendItems");
//        itemPosition=getIntent().getIntExtra("ItemPosition",0);

        int num = parentTend.getLikeNum();
//        MyLog.i("likeNum_ "+num);

        isItemComment = getIntent().getBooleanExtra("IsItemComment", false);
//        MyLog.d("TendDetails tendcontent_ "+parentTend.getContent());
    }

    @Override
    protected void initData() {

        myUser = BmobUser.getCurrentUser(MyUser.class);
//        if(parentTend.getCommentsList()==null)        {
//            parentTend.setCommentsList(new ArrayList<TendComments>());
//        }
//        dataList =parentTend.getCommentsList();
//        MyLog.d("initData commentList SIze_ "+dataList.size());

        isLike0ld = parentTend.isLike();


        if (dataList == null) {
//            MyLog.e("dataList null");
            dataList = new ArrayList<>();
        }
//        }else{
//            MyLog.v("dataList_size "+dataList.size());
//        }
        //dataList中装的都是 相关comment组成的集合啊
        BmobQuery<TendComments> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("tendItems",parentTend);
        query2.order("-createAt");
//        query2.include("tendItems");
        query2.findObjects(new FindListener<TendComments>() {
            @Override
            public void done(List<TendComments> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询所列评论成功");

                    dataList=list;
                    //setAdapter
                    mAdapter = new TendCommentAdapter(TendDetailsActivity.this, dataList);
//        setHeader(mRecyclerView);
                    // 传入参数到 mHeaderAndFooterWrapper中
                    setHeadAndFootViewWrapper();
                    mHeaderAndFooterWrapper.setOnItemCommentClickListener(TendDetailsActivity.this);
                    mHeaderAndFooterWrapper.setOnItemLikeClickListener(TendDetailsActivity.this);
//                    MyLog.v("setAdapter");
                    mRecyclerView.setAdapter(mHeaderAndFooterWrapper);

                }else{
                    MyLog.e("查询所列评论失败"+e.getMessage());
                    }//else
            }//done
        });




//        String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(TimeUtils2.stringToLong(parentTend.getCreatedAt(), TimeUtils2.FORMAT_DATE_TIME_SECOND));
//        tv_tendName.setText(parentTend.getFriendName());
//        tv_tendPubtime.setText(pubTime);
//        tv_tendPubcontent.setText(parentTend.getContent());
//        tv_commentNum.setText("" + parentTend.getCommentNum());
//        tv_likeNum.setText("" + parentTend.getLikeNum());
//
//        Glide.with(this).load(parentTend.getFriendHeadUrl()).into(roundIv_tendency_head);
//
//        if (parentTend.getPicUrlList() != null && !parentTend.getPicUrlList().get(0).equals("")) {
//            ninegrid.setVisibility(View.VISIBLE);
//            ninegrid.setImagesData(parentTend.getPicUrlList());
//        } else {
//           ninegrid.setVisibility(View.GONE);
//        }
    }//initData

    private void setHeadAndFootViewWrapper() {
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapperTendDetails(this, parentTend, mAdapter);
//        MyLog.v("aaa_ ");
        header = LayoutInflater.from(this).inflate(R.layout.recyclerview_comment_headitems, mRecyclerView, false);
        View footer = LayoutInflater.from(this).inflate(R.layout.recyclerview_comment_footitems, mRecyclerView, false);
        mHeaderAndFooterWrapper.addFootView(footer);
        mHeaderAndFooterWrapper.addHeaderView(header);

    }

    @Override
    public void onClick(View view) {
        //留言发送按钮
        String text = et_comment.getText().toString();
        if (text.equals(""))
            MyToast.showToast(this, "请输入文字后再发送");
        else {
//            MyLog.i(text);
            uploadComments(text);
        }
    }//onClick

    private void uploadComments(String comments) {
        MyUser commentUser = BmobUser.getCurrentUser(MyUser.class);
        MyUser tendPubUser = parentTend.getPubUser();

        TendComments subtendComments = new TendComments();
        subtendComments.setCommentUserName(commentUser.getUsername());
        subtendComments.setCommentContent(comments);
        subtendComments.setCommentUserHead(commentUser.getHeadUrl());

        //向评论TendComments中注入 ，和隶属动态项
        subtendComments.setTendItems(parentTend);
        subtendComments.setTendUser(tendPubUser);


        String pubTime = TimeUtils2.getCurrentTime(TimeUtils2.FORMAT_DATE_TIME_SECOND);
//        System.currentTimeMillis();
        subtendComments.setCommentPubtime(pubTime);
//        if (parentTend.getCommentsList()==null)
//            parentTend.setCommentsList(new ArrayList<TendComments>());

//            parentTend.getCommentsList().add(subtendComments);

//        MyLog.i("发送按钮中commentList size_ "+parentTend.getCommentsList().size());
//        parentTend.increment("commentNum");
//        if(dataList==null){
//            MyLog.e("dataList null");
//
//        }
        // 直接递增，让TendCommentAdapter2 直接增加item
        parentTend.setCommentNum(parentTend.getCommentNum() + 1);
        dataList.add(subtendComments);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        parentTend.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyLog.i("添加评论成功");
//                    setMsgBean(parentTend);
                } else {
                    MyLog.i("添加评论失败：" + e.getMessage());
//                    ll_sendComment.setVisibility(View.GONE);
                }
            }//done
        });//update

        subtendComments.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    MyLog.i("添加评论2成功");
                } else {
                    MyLog.e("添加评论2失败" + e.getMessage());
                }//else
            }//done
        });

        ll_sendComment.setVisibility(View.GONE);
        et_comment.setText("");
//        MyLog.i("我表示改了");


//        et_comment.setFocusable(false);
//        et_comment.clearFocus();

//        InputMethodManager inputManager = (InputMethodManager)et_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    //    private void setMsgBean(TendItems tend){
//
//    }
    @Override
    public void onBackPressed() {

        MyLog.i("Details backpress");
        Intent intent2 = new Intent();
        intent2.putExtra("ParentTend", parentTend);
//        if (parentTend.getCommentsList()!=null)
//              MyLog.d("commentSize_ "+parentTend.getCommentsList().size())  ;
//        else
//            MyLog.d("commentSize_ "+0);

        setResult(RESULT_BACK, intent2);
//        et_comment.clearFocus();
//        et_comment.setFocusable(false);
        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager imm = (InputMethodManager)
                et_comment.getContext().getSystemService(INPUT_METHOD_SERVICE);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            MyLog.i("key back");
            if (imm.isActive(et_comment)) {
                et_comment.clearFocus();
//                et_comment.setFocusable(false);
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onItemCommentClick(View v) {
        ll_sendComment.setVisibility(View.VISIBLE);
//        MyLog.i("onItem comment click2");
        et_comment.setFocusable(true);
        et_comment.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) et_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_comment, 0);
    }

    @Override
    public void onItemLikeClick(View v) {
//        MyLog.v("like click");
        // 直接递增，让TendCommentAdapter2 直接增加item
//        parentTend.setCommentNum(parentTend.getCommentNum()+1);
//        dataList.add(subtendComments);
        if (parentTend.isLike()) {//若为红色，点击即变为白色，点赞数减1
            parentTend.setLikeNum(parentTend.getLikeNum() - 1);
            parentTend.setLike(false);

            //删除关联
            relation = new BmobRelation();
            relation.remove(parentTend);
            myUser.setLikeTendItems(relation);
        } else {
            parentTend.setLikeNum(parentTend.getLikeNum() + 1);

            parentTend.setLike(true);

            relation = new BmobRelation();
            relation.add(parentTend);
//多对多关联指向`user`的`likeTendItems`字段
            myUser.setLikeTendItems(relation);
        }


        mHeaderAndFooterWrapper.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
//       MyLog.i("tendDetials  onPause");
        uploadLike();
        super.onPause();
    }


    private void uploadLike() {
        if (isLike0ld != parentTend.isLike()) {//若最终有变化,则上传
//            parentTend.setValue("isLike",parentTend.isLike());
//            if(parentTend.isLike())//若为红色，则加1
//                parentTend.setValue("likeNum",parentTend.getLikeNum()+1);
//            else
//                parentTend.setValue("likeNum",parentTend.getLikeNum()-1);

            parentTend.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        MyLog.i("点赞上传成功details");
                    } else
                        MyLog.e("点赞上传失败details");
                }
            });//update
        }//if isLike
    }
}//TendDetails_cls
