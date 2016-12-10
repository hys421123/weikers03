package com.team.witkers.activity.orders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
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
import com.team.witkers.activity.homeitem.ClaimTaskActivity2;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ChooseClaimant;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.TimeUtils2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.team.witkers.R.id.btn_confirm;

public class OrdersDoingShowActivity extends BaseActivity implements View.OnClickListener {
//任务详情，任务正在进行中的界面

    private ImageView ivHead, iv_topBack, iv_selectedhead;
    private TextView tvName, tvDistance, tvTime, tvDetail, tvMaxPrice,
            tvCompleteTime, tvAddress, tv_selectedname, tv_selectedprice;
    private Button btnSelect;
    private MsgOrdersBean msgOrdersBean;
    private Mission mission;
    private Handler mHandler = new Handler();
    private int claimPosition;
    private String stateStr;
    private ImageButton ib_call;
    //    private ChooseClaimant claimant;
    private String phoneNum;

    //别人选择你，你帮别人完成任务
    private boolean taskDoing;

    @Override
    protected int setContentId() {
        return R.layout.activity_orders_doing_show_2;
    }

    @Override
    protected void initView() {
        ib_call = (ImageButton) findViewById(R.id.ib_call);
        btnSelect = (Button) findViewById(btn_confirm);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        tvMaxPrice = (TextView) findViewById(R.id.tv_maxprice);
        tvCompleteTime = (TextView) findViewById(R.id.tv_completetime);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        iv_selectedhead = (ImageView) findViewById(R.id.iv_selectedhead);
        tv_selectedname = (TextView) findViewById(R.id.tv_selectedname);
        tv_selectedprice = (TextView) findViewById(R.id.tv_selectedprice);
    }

    @Override
    protected void setListener() {
        btnSelect.setOnClickListener(this);
        iv_topBack.setOnClickListener(this);
        ib_call.setOnClickListener(this);
    }

    @Override
    protected void getIntentData() {
        msgOrdersBean = (MsgOrdersBean) getIntent().getSerializableExtra("fromMsgOrdersBeanAdapter");
//        MyLog.i("content-->" + msgOrdersBean.getOrderContent() + "--stateInfo-->" + getIntent().getStringExtra("stateInfo"));
        mission = queryMission(msgOrdersBean.getMissionId());//得到mission对象，设置mission详情的显示
        stateStr = getIntent().getStringExtra("stateInfo");
        MyLog.d("stateStr_ "+stateStr);

        taskDoing=getIntent().getBooleanExtra("taskDoing",false);

        MyLog.v("taskDoing_ "+taskDoing);
        if (stateStr == null) {
            stateStr = "default";
        }
    }

    @Override
    protected void showView() {


        switch (stateStr) {
            case "任务正在进行中":
                if(taskDoing){
                    btnSelect.setText("点击任务完成");
//                    btnSelect.setBackgroundColor(Color.rgb(166,205,237));
                }else{
                    MyLog.i("show_view设置");
                    btnSelect.setClickable(false);
                    btnSelect.setText("您的任务正在进行中...");
                    btnSelect.setBackgroundResource(R.color.Orders_confirmed);
                }
//                btnSelect.setBackgroundResource(R.color.Orders_confirmed);

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

    private Mission queryMission(final String missionId) {
        final BmobQuery<Mission> query = new BmobQuery<Mission>();
        query.getObject(missionId, new QueryListener<Mission>() {
            @Override
            public void done(final Mission mission, BmobException e) {
                MyLog.i("mission 查询成功");
                OrdersDoingShowActivity.this.mission = mission;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String headUrl = mission.getPubUserHeadUrl();
                        if (headUrl == null || headUrl.equals("")) {
                            Glide.with(OrdersDoingShowActivity.this).load(R.drawable.default_head).into(ivHead);
                        } else {
                            Glide.with(OrdersDoingShowActivity.this).load(headUrl).into(ivHead);
                        }
                        tvName.setText(mission.getPubUserName());
                        tvDistance.setText("---");
                        String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                                TimeUtils2.stringToLong(mission.getCreatedAt(), TimeUtils2.FORMAT_DATE_TIME_SECOND));
                        tvTime.setText(pubTime);
                        SetDetails(mission.getInfo());//设置信息详情
                        tvMaxPrice.setText(mission.getCharges() + "");
                        tvCompleteTime.setText(mission.getFinishTime());
                        tvAddress.setText(mission.getAddress());
                        ChooseClaimant claimant = mission.getChooseClaimant();
                        Glide.with(OrdersDoingShowActivity.this).load(claimant.getClaimHeadUrl()).into(iv_selectedhead);
                        tv_selectedname.setText(claimant.getClaimName());
                        tv_selectedprice.setText(claimant.getClaimMoney() + "");
                        queryClaimant(claimant);

                    }
                });
            }

            private void queryClaimant(ChooseClaimant claimant) {
                BmobQuery<MyUser> query2 = new BmobQuery<MyUser>();
                query2.addWhereEqualTo("username", claimant.getClaimName());
                query2.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        if (e == null) {
                            MyLog.i("查询唯一认领人成功");
                            phoneNum = list.get(0).getMobilePhoneNumber();
                            MyLog.v("phoneNum_ " + phoneNum);
                        } else {
                            MyLog.e("查询唯一认领人失败" + e.getMessage());
                        }//else
                    }//done
                });
            }//queryClaimant

            private void SetDetails(String info) {
                SpannableString spannableString = new SpannableString(info);
                int index1 = info.indexOf("#");
                if (index1 != -1) {
                    int index2 = info.indexOf("#", index1 + 1);
                    if (index2 != -1) {
                        //NoUnderlineClickableSpan 设置为不要下划线的类
                        spannableString.setSpan(new NoUnderlineClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                //TODO 这里修改跳转的目标activity
                                Intent intent = new Intent(OrdersDoingShowActivity.this, ClaimTaskActivity2.class);
                       /* intent.putExtra("fromTakeOutMissionAdapterLIN",dataList.get(position));*/
                                Toast.makeText(OrdersDoingShowActivity.this, "label has been clicked", Toast.LENGTH_SHORT).show();
                                OrdersDoingShowActivity.this.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(Color.rgb(74, 125, 174));
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
        for (int i = 0; i < claimItemsList.size(); i++) {
            if (claimItemsList.get(i).getClaimFlag() != 0) {
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
            case btn_confirm:
//                MyToast.showToast(this, "确定已完成！");
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("是否真的已经完成了任务？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //关闭dialog
                        updateMission();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        Toast.makeText(OrdersDoingShowActivity.this, "取消" + which, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
//                List<ClaimItems> claimItemsList = mission.getClaimItemList();
//                isSelected(claimItemsList);
//                //有问题
//                mission.getClaimItemList().get(claimPosition).setClaimFlag(0);
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
//                mission.setDealTime(new Date(System.currentTimeMillis()));
//                mission.update(mission.getObjectId(), new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            MyLog.i("更新成功");
//                            btnSelect.setBackgroundResource(R.color.Orders_confirmed);
//                            btnSelect.setClickable(false);
//                            btnSelect.setText("任务已完成，等待评价");
//                        } else {
//                            MyLog.i("更新失败：" + e.getMessage() + "," + e.getErrorCode());
//                        }
//                    }
//                });
                break;
            case R.id.ib_call:
                MyLog.i("ib_call");
                if (phoneNum != null) {
                    MyLog.i(phoneNum);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 111);
                    }

                    Intent intent3 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        MyLog.e("check error");
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //android 6.0
                        return;
                    }
                    startActivity(intent3);
                }
                else
                   MyLog.e("phone_null");
                break;
        }
    }//onClick

    private void updateMission(){

       ChooseClaimant claimant=mission.getChooseClaimant();
        // 任务 完成标志位
        claimant.setClaimStatus(true);
        mission.setFinished(true);
        // 填写 完成 交易时间
        mission.setDealTime(new Date(System.currentTimeMillis()));
        mission.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyLog.i("任务完成 上传成功");
                    btnSelect.setClickable(false);
                   btnSelect.setText("任务已完成，等待评价");
                    btnSelect.setBackgroundResource(R.color.Orders_confirmed);
                    Toast.makeText(OrdersDoingShowActivity.this, "恭喜您已经完成了任务" , Toast.LENGTH_SHORT).show();

                }else{
                    MyLog.e("任务完成 上传失败"+e.getMessage());
                    }//else
            }//done
        });

    }//update mission

}//OrdersDoingShowAct
