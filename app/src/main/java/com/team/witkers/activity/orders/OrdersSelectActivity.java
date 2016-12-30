package com.team.witkers.activity.orders;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.homeitem.NoUnderlineClickableSpan;
import com.team.witkers.activity.homeitem.ClaimTaskActivity2;
import com.team.witkers.adapter.OrdersShowAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.TimeUtils2;
import com.zfdang.multiple_images_selector.DividerItemDecoration;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;



public class OrdersSelectActivity extends BaseActivity implements View.OnClickListener{
//  消息frm到任务详情，选择某微客支付的 activity
    private MsgOrdersBean msgOrdersBean;
    private ImageView ivHead,iv_topBack;
    private TextView tvName,tvDistance,tvTime,tvDetail,tvMaxPrice,tvCompleteTime,tvAddress,tv_topTitle;
    private Button btnSelect;
    private Handler mHandler = new Handler();
    private List<ClaimItems> claimItemsList;
    private OrdersShowAdapter ordersShowAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView mRecyclerView;
    private static final int PAY_FLAG = 1;
    private Mission mission;
    private String takerName;
    private int position;

    @Override
    protected int setContentId() {
        return R.layout.activity_orders_show_2;

    }

    @Override
    protected void getIntentData(){

        MyLog.v("OrdersSelectActivity getIntent");

        msgOrdersBean = (MsgOrdersBean) getIntent().getSerializableExtra("fromMsgOrdersBeanAdapter");
        MyLog.i("content-->"+msgOrdersBean.getOrderContent());

        claimItemsList = msgOrdersBean.getClaimItemsList();//得到list，设置list的显示
        Mission mission = queryMission(msgOrdersBean.getMissionId());//得到mission对象，设置mission详情的显示
    }

    @Override
    protected void initView() {
        btnSelect = (Button) findViewById(R.id.btn_select);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        tvMaxPrice = (TextView) findViewById(R.id.tv_maxprice);
        tvCompleteTime = (TextView) findViewById(R.id.tv_completetime);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tv_topTitle = (TextView) findViewById(R.id.tv_topTitle);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_orders_select);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.getOrientation(),0));
    }

    @Override
    protected void setListener() {
        btnSelect.setOnClickListener(this);
        iv_topBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {


        ordersShowAdapter = new OrdersShowAdapter(this,claimItemsList);
        mRecyclerView.setAdapter(ordersShowAdapter);

        ordersShowAdapter.setOnItemClickListener(new OrdersShowAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data, int position) {
//                MyToast.showToast(OrdersSelectActivity.this,"data--->"+data+"----position--->"+position);
                takerName = data;
                OrdersSelectActivity.this.position = position;
            }
        });
    }

    private Mission queryMission(final String missionId){
        BmobQuery<Mission> query = new BmobQuery<Mission>();
        query.getObject(missionId, new QueryListener<Mission>() {
            @Override
            public void done(final Mission mission, BmobException e) {
                MyLog.i("mission 查询成功");
                OrdersSelectActivity.this.mission = mission;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String headUrl = mission.getPubUserHeadUrl();
                        if(headUrl==null||headUrl.equals("")){
                            Glide.with(OrdersSelectActivity.this).load(R.drawable.default_head).into(ivHead);
                        }else{
                            Glide.with(OrdersSelectActivity.this).load(headUrl).into(ivHead);
                        }
                        tvName.setText(mission.getPubUserName());
                        tvDistance.setText("---");
                        String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                                TimeUtils2.stringToLong(mission.getCreatedAt(), TimeUtils2.FORMAT_DATE_TIME_SECOND));
                        tvTime.setText(pubTime);
                        SetDetails(mission.getInfo());//设置信息详情
                        tvMaxPrice.setText(mission.getCharges()+"");
                        tvCompleteTime.setText(mission.getFinishTime());
                        tvAddress.setText(mission.getAddress());
                    }
                });
            }

            private void SetDetails(String info) {
                SpannableString spannableString = new SpannableString(info);
                int index1 = info.indexOf("#");
                if (index1!=-1){
                    int index2 = info.indexOf("#", index1 + 1);
                    if(index2!=-1){
                        //NoUnderlineClickableSpan 设置为不要下划线的类
                        spannableString.setSpan(new NoUnderlineClickableSpan() {
                            @Override
                            public void onClick(View widget){
                                //TODO 这里修改跳转的目标activity
                                Intent intent = new Intent(OrdersSelectActivity.this,ClaimTaskActivity2.class);
                       /* intent.putExtra("fromTakeOutMissionAdapterLIN",dataList.get(position));*/
                                Toast.makeText(OrdersSelectActivity.this,"label has been clicked",Toast.LENGTH_SHORT).show();
                                OrdersSelectActivity.this.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(Color.rgb(74,125,174));
                            }
                        }, index1, index2 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                //设置任务详情
                tvDetail.setText(spannableString);
                tvDetail.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
        return null;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.btn_select:
                if(takerName==null){
                    MyToast.showToast(OrdersSelectActivity.this,"你还没有选择微客哦！");
                    return;
                }
                new AlertDialog.Builder(OrdersSelectActivity.this).setTitle("确定选择该微客？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MyToast.showToast(OrdersSelectActivity.this,"应该跳转了");
                                Intent intent = new Intent(OrdersSelectActivity.this,OrdersPayActivity.class);
                                intent.putExtra("fromOrdersShow",mission);
                                intent.putExtra("position",position);
                                startActivityForResult(intent,PAY_FLAG);
                                
/*                                MyToast.showToast(OrdersSelectActivity.this,"选择"+takerName+"完成你的任务");
                                //传递takerName和mission对象
                                mission.getClaimItemList().get(position).setClaimFlag("Doing");
                                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
                                mission.setDealTime(new Date(System.currentTimeMillis()));
                                mission.update(mission.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            MyLog.i("更新成功");
                                            Intent intent = new Intent(OrdersSelectActivity.this,OrdersPayActivity.class);
                                            intent.putExtra("fromOrdersShow",mission);
                                            startActivity(intent);
                                        }else{
                                            MyLog.i("更新失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });*/
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //result_ok 接收来自orderPayActivity的finsh事件传递的参数
        if (resultCode == OrdersPayActivity.RESULT_CODE) {//未登陆返回 canceled
            MyLog.v("参数返回");
            btnSelect.setText("任务正在进行中");

            ClaimItems claimItem=(ClaimItems) data.getSerializableExtra("claimItem");
            claimItemsList.clear();
            claimItemsList.add(claimItem);

            ordersShowAdapter.notifyDataSetChanged();
        }//if
    }//onActivityResult
}//OderShowAct_cls
