package com.team.witkers.activity.orders;

import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.homeitem.NoUnderlineClickableSpan;
//import com.team.witkers.activity.homeitem.TaskDetailsActivity;
import com.team.witkers.activity.homeitem.TaskDetailsActivity2;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ChooseClaimant;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.TimeUtils2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class OrdersDoingShowActivity extends BaseActivity implements View.OnClickListener{
//任务详情，任务正在进行中的界面

    private ImageView ivHead,iv_topBack,iv_selectedhead;
    private TextView tvName,tvDistance,tvTime,tvDetail,tvMaxPrice,
            tvCompleteTime,tvAddress,tv_selectedname,tv_selectedprice;
    private Button btnSelect;
    private MsgOrdersBean msgOrdersBean;
    private Mission mission;
    private Handler mHandler = new Handler();
    private int claimPosition;
    private String stateStr;
    private ImageButton ib_call;

    @Override
    protected int setContentId() {
        return R.layout.activity_orders_doing_show;
    }

    @Override
    protected void initView() {
        ib_call= (ImageButton) findViewById(R.id.ib_call);
        btnSelect = (Button) findViewById(R.id.btn_confirm);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        tvMaxPrice = (TextView) findViewById(R.id.tv_maxprice);
        tvCompleteTime = (TextView) findViewById(R.id.tv_completetime);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        iv_selectedhead=(ImageView)findViewById(R.id.iv_selectedhead);
        tv_selectedname= (TextView) findViewById(R.id.tv_selectedname);
        tv_selectedprice= (TextView) findViewById(R.id.tv_selectedprice);
    }

    @Override
    protected void setListener() {
        btnSelect.setOnClickListener(this);
        iv_topBack.setOnClickListener(this);
        ib_call.setOnClickListener(this);
    }

    @Override
    protected void getIntentData(){
        msgOrdersBean = (MsgOrdersBean) getIntent().getSerializableExtra("fromMsgOrdersBeanAdapter");
        MyLog.i("content-->"+msgOrdersBean.getOrderContent()+"--stateInfo-->"+getIntent().getStringExtra("stateInfo"));
        Mission mission = queryMission(msgOrdersBean.getMissionId());//得到mission对象，设置mission详情的显示
        stateStr = getIntent().getStringExtra("stateInfo");
        if(stateStr==null){
            stateStr = "default";
        }
    }

    @Override
    protected void showView() {
        switch (stateStr){
            case "任务正在进行中":
                btnSelect.setBackgroundResource(R.color.Orders_confirmed);
                btnSelect.setClickable(false);
                btnSelect.setText("您的任务正在进行中...");
                break;
            case "任务已完成":
                btnSelect.setBackgroundResource(R.color.Orders_confirmed);
                btnSelect.setClickable(false);
                btnSelect.setText("任务已完成，等待评价");
                break;
            default:
                MyLog.i(stateStr);
                break;
        }
    }

    private Mission queryMission(final String missionId){
        BmobQuery<Mission> query = new BmobQuery<Mission>();
        query.getObject(missionId, new QueryListener<Mission>() {
            @Override
            public void done(final Mission mission, BmobException e) {
                MyLog.i("mission 查询成功");
                OrdersDoingShowActivity.this.mission = mission;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String headUrl = mission.getPubUserHeadUrl();
                        if(headUrl==null||headUrl.equals("")){
                            Glide.with(OrdersDoingShowActivity.this).load(R.drawable.default_head).into(ivHead);
                        }else{
                            Glide.with(OrdersDoingShowActivity.this).load(headUrl).into(ivHead);
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
                       ChooseClaimant claimant= mission.getChooseClaimant();
                        Glide.with(OrdersDoingShowActivity.this).load(claimant.getClaimHeadUrl()).into(iv_selectedhead);
                        tv_selectedname.setText(claimant.getClaimName());
                        tv_selectedprice.setText(claimant.getClaimMoney()+"");
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
                                Intent intent = new Intent(OrdersDoingShowActivity.this,TaskDetailsActivity2.class);
                       /* intent.putExtra("fromTakeOutMissionAdapterLIN",dataList.get(position));*/
                                Toast.makeText(OrdersDoingShowActivity.this,"label has been clicked",Toast.LENGTH_SHORT).show();
                                OrdersDoingShowActivity.this.startActivity(intent);
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

    //有问题
    private Boolean isSelected(List<ClaimItems> claimItemsList) {
        for(int i=0;i<claimItemsList.size();i++){
            if(claimItemsList.get(i).getClaimFlag()!=0){
                claimPosition = i;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.btn_confirm:
                MyToast.showToast(this,"确定已完成！");
                List<ClaimItems> claimItemsList =mission.getClaimItemList();
                isSelected(claimItemsList);
                //有问题
                mission.getClaimItemList().get(claimPosition).setClaimFlag(0);
                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss ");
                mission.setDealTime(new Date(System.currentTimeMillis()));
                mission.update(mission.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            MyLog.i("更新成功");
                            btnSelect.setBackgroundResource(R.color.Orders_confirmed);
                            btnSelect.setClickable(false);
                            btnSelect.setText("任务已完成，等待评价");
                        }else{
                            MyLog.i("更新失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                break;
            case R.id.ib_call:
                MyLog.i("ib_call");

                break;
        }
    }
}
