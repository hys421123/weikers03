package com.team.witkers.activity.homeitem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.find.SearchActivity;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.TimeUtils2;
import com.team.witkers.utils.liteasysuits.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/9/6.
 */
public class ClaimTaskActivity2 extends BaseActivity implements View.OnClickListener{
// 任务详情，认领任务
    private Mission myMission;
    private MyUser myUser;
    private ImageView iv_head,iv_topback;//用户图像
    private TextView tv_name,tv_distance,tv_detail,tv_completetime,
            tv_address,tv_getdetails,tv_maxprice,tv_time,tv_gettask;
    private EditText et_price;//认领金钱
    private RelativeLayout rl_gettask;//认领任务
    //这个List用来存储所有发布的任务对象
    private List<Mission> dataList = new ArrayList<>();
    private float claimMoney;
    private  List<MyUser> listUser=null;

    @Override
    protected int setContentId() {
        return R.layout.activity_task_details_2;
    }

    @Override
    protected void initView() {
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_topback = (ImageView) findViewById(R.id.iv_topBack);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_completetime = (TextView) findViewById(R.id.tv_completetime);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_getdetails = (TextView) findViewById(R.id.tv_getdetails);
        tv_maxprice = (TextView) findViewById(R.id.tv_maxprice);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_gettask = (TextView) findViewById(R.id.tv_gettask);
        et_price = (EditText) findViewById(R.id.et_price);
        // 确定认领任务按钮
        rl_gettask = (RelativeLayout) findViewById(R.id.rl_gettask);

        myUser = BmobUser.getCurrentUser(MyUser.class);
    }

    @Override
    protected void getIntentData() {
//        MyLog.i("TaskDetailsAct_getIntent ");
        myMission = (Mission) getIntent().getSerializableExtra("fromTakeOutMissionAdapterLIN");
//        MyLog.v("missionContent_ "+myMission.getInfo());
    }

    @Override
    protected void setListener() {
        rl_gettask.setOnClickListener(this);
        iv_topback.setOnClickListener(this);
    }



    //设置按钮的可点击性
    private void setUnEditable(){
        MyLog.i("此任务已经认领过了");
        tv_gettask.setText("已认领此任务");
        et_price.setEnabled(false);
        rl_gettask.setBackgroundResource(R.color.complete_take_mission_bg);
        rl_gettask.setClickable(false);
    }

    @Override
    protected void showView() {
        if(isContentLable(myMission.getInfo())){
            tv_detail.setText(getLableLink(myMission.getInfo()));
            tv_detail.setMovementMethod(LinkMovementMethod.getInstance());//这句不能掉
        }else{
            tv_detail.setText(myMission.getInfo());
        }
        tv_name.setText(myMission.getName());
        tv_address.setText(myMission.getAddress());
        tv_completetime.setText(myMission.getFinishTime());
        tv_maxprice.setText(myMission.getCharges()+"");
        String date = myMission.getCreatedAt();//获得日期
        String time = TimeUtils2.getDescriptionTimeFromTimestamp(
                TimeUtils2.stringToLong(date, TimeUtils2.FORMAT_DATE_TIME_SECOND));
        tv_time.setText(time);
        if(myMission!=null&&myMission.getPubUserHeadUrl()!=null){
            String headUrl=myMission.getPubUser().getHeadUrl();
            Glide.with(this)
                    .load(headUrl)
                    .into(iv_head);
        }else{
            Glide.with(this)
                    .load(R.drawable.default_head)
                    .into(iv_head);
        }

    }//showView

    private Boolean isContentLable(String infoTemp){
        int index1 = infoTemp.indexOf("#");
        if (index1!=-1) {
            int index2 = infoTemp.indexOf("#", index1 + 1);
            if (index2 != -1) {
                return true;
            }else return false;
        }else
            return false;
    }

    private SpannableString getLableLink(final String infoTemp){
        final int index1 = infoTemp.indexOf("#");
        final int index2 = infoTemp.indexOf("#", index1 + 1);
        SpannableString spannableString = new SpannableString(infoTemp);
                //NoUnderlineClickableSpan 设置为不要下划线的类
                spannableString.setSpan(new NoUnderlineClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        MyToast.showToast(ClaimTaskActivity2.this,"onclick lable");
                        //TODO 这里修改跳转的目标activity
                        Intent intent = new Intent(ClaimTaskActivity2.this, SearchActivity.class);
                        String lableStr = infoTemp.substring(index1+1, index2 );
                        intent.putExtra("ToHotLableDetails",lableStr);
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.rgb(74,125,174));
                    }
                }, index1, index2 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

/*    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.i("Test","Flag-->"+takeMissionFlag);
                    tv_getdetails.setText("已有"+missiontakerNum+"名微客认领此任务");
                    if(takeMissionFlag){
                        setUnEditable();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };*/
    @Override
    protected void initData() {

//        MyLog.i("ObjId_ "+myMission.getObjectId());
        //查询认领人数及是否认领
        BmobQuery<Mission> query2=new BmobQuery<>();
        query2.getObject(myMission.getObjectId(), new QueryListener<Mission>() {
            @Override
            public void done(Mission mission, BmobException e) {
                if(e==null){
                    MyLog.i("查询认领成功");
//                    MyLog.i("查询认领成功，判断是否用户已经认领");
                    int claimSize=0;
                    //判断该用户是否已经认领
                    if(mission.getClaimItemList()!=null) {
                        claimSize = mission.getClaimItemList().size();
                        List<String> listName = new ArrayList<>();
                        for (ClaimItems item : mission.getClaimItemList()) {
                            listName.add(item.getClaimName());
                        }
                        if (listName.contains(MyApplication.mUser.getUsername())) {
//                            MyLog.i("用户已经认领过了");
                            setUnEditable();
                        }
                    }//getClaimItem not null
  // TODO: 2016/10/31  确定认领人数

                        tv_getdetails.setText("已有"+claimSize+"名微客认领此任务");
//                    }//ClaimItemList null
                }else{
                    MyLog.e("查询认领失败"+e.getMessage());
                    }//else
            }//done
        });

        //查询认领人数
//        BmobQuery<MyUser> query=new BmobQuery<>();
//        query.addWhereRelatedTo("missionTaker",new BmobPointer(myMission));
//        query.findObjects(new FindListener<MyUser>() {
//            @Override
//            public void done(List<MyUser> list, BmobException e) {
//                if(e==null){
//                    MyLog.i("查询认领人数成功");
//                    listUser=list;
//                }else{
//                    MyLog.e("查询认领人数失败"+e.getMessage());
//                    }//else
//
//            }//done
//        });
    }//initData


    //getTask  接收认领任务
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            // 确定认领任务
            case R.id.rl_gettask:
                if(MyApplication.mUser.getUsername().equals(myMission.getPubUserName()) ){
                    MyLog.i("yourself");
                    MyToast.showToast(this,"亲，自己是不能认领自己的任务哦！");
                    return;
                }
                if(BmobUser.getCurrentUser(MyUser.class)==null){
                    MyToast.showToast(this,"亲，你还未登陆哦！");
                    return;
                }

                String priceStr = et_price.getText().toString();
                //取出认领金额到 变量中   et_price只能输入数字，小数

                if(priceStr!=null&&!priceStr.equals("")){
                    claimMoney=Float.valueOf(priceStr);
                    if(claimMoney > myMission.getCharges()){
                        MyToast.showToast(this,"您的认领金额不能大于佣金");
                        return;
                    }
                    new AlertDialog.Builder(ClaimTaskActivity2.this).setTitle("确定认领此任务吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //TODO 添加一个标志，用来判断任务是否被认领
                                    Toast.makeText(ClaimTaskActivity2.this, "确定认领此任务", Toast.LENGTH_SHORT).show();

                                    //认领此任务，向服务器提交数据
                                    claimMission();
                                    setUnEditable();

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ClaimTaskActivity2.this, "取消", Toast.LENGTH_SHORT).show();
                                }
                            }).show();

                }else{
                    Toast.makeText(ClaimTaskActivity2.this, "请填入认领金额", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.iv_topBack:
                finish();
                break;
        }

    }//onClick
    private void claimMission(){
        BmobRelation relation = new BmobRelation();
        BmobRelation relation2 = new BmobRelation();
    //将当前认领用户添加到多对多关联中
        relation.add(MyApplication.mUser);
        relation2.add(myMission);
    //多对多关联指向`mission`的`MissionTaker`字段
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String curTime = formatter.format(curDate);

        if(myMission.getClaimItemList()==null){
          MyLog.e("claimItemList null");
          List<ClaimItems> claimItemList=new ArrayList<>();
            String headUrl = BmobUser.getCurrentUser(MyUser.class).getHeadUrl();
          ClaimItems claimItem=new ClaimItems(MyApplication.mUser.getUsername(),claimMoney,
                  headUrl,curTime,0);
            MyLog.i("BmobUser.getCurrentUser(MyUser.class)---领取任务的第1人-->"+ BmobUser.getCurrentUser(MyUser.class).getHeadUrl());
            MyLog.i("MyApplication.mUser.getHeadUrl()---领取任务的第一个人-->"+MyApplication.mUser.getHeadUrl());
          claimItemList.add(claimItem);
          myMission.setClaimItemList(claimItemList);

      }else{
            MyLog.v("claimList not null");
            String headUrl = BmobUser.getCurrentUser(MyUser.class).getHeadUrl();
            MyLog.i("BmobUser.getCurrentUser(MyUser.class)---领取任务的第n人-->"+ BmobUser.getCurrentUser(MyUser.class).getHeadUrl());
            MyLog.i("MyApplication.mUser.getHeadUrl()---领取任务的第n人-->"+MyApplication.mUser.getHeadUrl());
          myMission.getClaimItemList().add(new ClaimItems(MyApplication.mUser.getUsername(),claimMoney,
                  headUrl,curTime,0));
      }
        myMission.setMissionTaker(relation);
        myMission.update(myMission.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyLog.i("mission多对多关联认领成功");
                }else{
                    MyLog.e("多对多关联认领失败"+e.getMessage());
                    }//else
            }
        });//update myMission

        myUser.setTakeMissions(relation2);
        myUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                MyLog.i("user多对多关联认领成功");
            }
        });
    }//claimMission
}//TaskDetailsAct2_cls
