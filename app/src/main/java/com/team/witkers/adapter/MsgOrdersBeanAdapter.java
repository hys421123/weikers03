package com.team.witkers.adapter;

/**
 * Created by jin on 2016/9/12.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.orders.OrdersDoingShowActivity;
import com.team.witkers.activity.orders.OrdersEvaluateShowActivity;
import com.team.witkers.activity.orders.OrdersSelectActivity;
import com.team.witkers.activity.orders.WeikersMsgActivity;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.MsgOrdersBean;
import com.team.witkers.utils.TimeUtils2;
import com.team.witkers.views.ItemLongOnClickDialog;

import java.util.List;

public class MsgOrdersBeanAdapter extends RecyclerView.Adapter<MsgOrdersBeanAdapter.MyViewHolder> {
    private List<MsgOrdersBean> dataListMsgOrdersBean;
    private List<ClaimItems> claimItemsList;
    private Context context;
    private int Num = 0;
    private MsgOrdersBean msgOrdersBean;
    SpannableString spannableString;
    private String pubTime;
    private  int claimPosition;//认领用户在claimList中的位置

    public MsgOrdersBeanAdapter(Context context, List<MsgOrdersBean> dataList){
        this.context = context;
        this.dataListMsgOrdersBean = dataList;
    }

    @Override
    public MsgOrdersBeanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleview_orders_item_2, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MsgOrdersBeanAdapter.MyViewHolder holder, final int position) {



        msgOrdersBean = dataListMsgOrdersBean.get(position);
        claimItemsList = msgOrdersBean.getClaimItemsList();

//        private int claimFlag;//认领标志位
//        0. 微客消息通知， 即你被 别人确认 选中接单

        // 1.有认领人数的，没有确定认领人的；
        // 2.有确定认领人的，任务正在进行中的；
        // 3.有确定认领人的，任务已经完成的；

        switch (msgOrdersBean.getClaimFlag()){
//            0. 微客消息通知， 即你被 别人确认 选中接单
            case 0:
                // 设置 微客消息通知

             String s=   msgOrdersBean.getRecentTakerUrl();
                MyLog.v("urlNum_ "+s);
                if(s.equals("0"))//若为0,则为无 小红点
                      Glide.with(context).load(R.drawable.notice2).into(holder.ivPubUser);
                else// 若为1， 则为有小红点的
                    Glide.with(context).load(R.drawable.notice2_red).into(holder.ivPubUser);

                holder.tvDetails.setText("接单消息");
                holder.tvTime.setVisibility(View.GONE);
                break;


            // 1.有认领人数的，没有确定认领人的；
            case 1:
//                    MyLog.i("你的任务xx已被xx人认领");
                String infoStr = msgOrdersBean.getOrderContent();
                int takerNum = msgOrdersBean.getTakerNum();
                String headUrl = msgOrdersBean.getRecentTakerUrl();
                String time = msgOrdersBean.getRecentTakeTime();
                if(time!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }
                if(infoStr.length()>11){
                    infoStr = infoStr.substring(0,11)+"...";
                }
                spannableString = new SpannableString("任务 "+infoStr+" 已有 "+takerNum+" 名微客认领");
                spannableString.setSpan( new ForegroundColorSpan(Color.rgb(85, 85, 85)), 3, infoStr.length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(headUrl==null||headUrl.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl).into(holder.ivPubUser);
                }
                holder.tvDetails.setText(spannableString);
                holder.tvTime.setText(pubTime);
                break;

            // 2.有确定认领人的，任务正在进行中的；
            case 2:
                // 2.选择微客后，发布方收到消息;

//                    MyLog.i("xx选择了你，请尽快完成任务");

                String infoStr2 = msgOrdersBean.getOrderContent();
                String headUrl2 = msgOrdersBean.getRecentTakerUrl();
                String time2 = msgOrdersBean.getRecentTakeTime();
                if(time2!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time2, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }
                if(infoStr2.length()>11){
                    infoStr = infoStr2.substring(0,11)+"...";
                }
                spannableString = new SpannableString("任务 "+infoStr2+" 正在进行中");
                spannableString.setSpan( new ForegroundColorSpan(Color.rgb(85, 85, 85)), 3, infoStr2.length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(headUrl2==null||headUrl2.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl2).into(holder.ivPubUser);
                }
                holder.tvDetails.setText(spannableString);
                holder.tvTime.setText(pubTime);
                break;



            // 3.有确定认领人的，任务已经完成的；
            case 3:

                String infoStr3 = msgOrdersBean.getOrderContent();
                String headUrl3 = msgOrdersBean.getRecentTakerUrl();
                String time3 = msgOrdersBean.getRecentTakeTime();
                if(time3!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time3, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }
                if(infoStr3.length()>11){
                    infoStr3 = infoStr3.substring(0,11)+"...";
                }
                spannableString = new SpannableString("任务 "+infoStr3+" 已完成，请评价");
                spannableString.setSpan( new ForegroundColorSpan(Color.rgb(85, 85, 85)), 3, infoStr3.length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(headUrl3==null||headUrl3.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl3).into(holder.ivPubUser);
                }
                holder.tvDetails.setText(spannableString);
                holder.tvTime.setText(pubTime);
                break;
            case 4:
                // 4.选择微客后，认领方收到消息;

                // MyLog.i("你认领的任务xx正已完成，等待对方评价！");
                String infoStr4 = msgOrdersBean.getOrderContent();
                String headUrl4 = msgOrdersBean.getRecentTakerUrl();
                String time4 = msgOrdersBean.getRecentTakeTime();
                if(time4!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time4, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }
                if(infoStr4.length()>10){
                    infoStr3 = infoStr4.substring(0,10)+"...";
                }
                spannableString = new SpannableString(infoStr4+" 选择了你，请尽快完成任务");
                // 对方用户名+" 选择了你，请尽快完成任务"
                spannableString.setSpan( new ForegroundColorSpan(Color.BLACK), 7, infoStr4.length()+7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(headUrl4==null||headUrl4.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl4).into(holder.ivPubUser);
                }
                holder.tvDetails.setText(spannableString);
                holder.tvTime.setText(pubTime);
                break;
            case 5:
                // 5.发布方评价后，认领方收到消息;

                // MyLog.i("你的任务xx已完成，请及时评价！");
                String infoStr5 = msgOrdersBean.getOrderContent();
                String headUrl5 = msgOrdersBean.getRecentTakerUrl();
                String time5 = msgOrdersBean.getRecentTakeTime();
                if(time5!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time5, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }
                if(infoStr5.length()>10){
                    infoStr5 = infoStr5.substring(0,10)+"...";
                }
                spannableString = new SpannableString(infoStr5+" 已对你做出了评价");
                // 对方用户名+" 已对你做出了评价"
                spannableString.setSpan( new ForegroundColorSpan(Color.BLACK), 5, infoStr5.length()+5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if(headUrl5==null||headUrl5.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl5).into(holder.ivPubUser);
                }
                holder.tvDetails.setText(spannableString);
                holder.tvTime.setText(pubTime);
                break;
//            6. 有人选择了你， 接单任务
            case 6:
                String pubUserName6 = msgOrdersBean.getPubUserName();
                String headUrl6 = msgOrdersBean.getRecentTakerUrl();
                String time6 = msgOrdersBean.getRecentTakeTime();
                if(time6!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time6, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }


                spannableString = new SpannableString(pubUserName6+"选择了你，请尽快完成任务");
                // 对方用户名+" 已对你做出了评价"
                spannableString.setSpan( new ForegroundColorSpan(Color.rgb(74,125,174)), 0, pubUserName6.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                if(headUrl6==null||headUrl6.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl6).into(holder.ivPubUser);
                }
                holder.tvTime.setText(time6);
                holder.tvDetails.setText(spannableString);
                break;

//            7.你完成了别人 指定的任务/
            case 7:
                String infoStr7 = msgOrdersBean.getOrderContent();
                String headUrl7 = msgOrdersBean.getRecentTakerUrl();
                String time7 = msgOrdersBean.getRecentTakeTime();
                if(time7!=null){
                    pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(
                            TimeUtils2.stringToLong(time7, TimeUtils2.FORMAT_DATE_TIME_SECOND));
                }
                String details7="任务"+infoStr7+"   已经完成";


                if(headUrl7==null||headUrl7.equals("")){
                    Glide.with(context).load(R.drawable.default_head).into(holder.ivPubUser);
                }else{
                    Glide.with(context).load(headUrl7).into(holder.ivPubUser);
                }
                holder.tvTime.setText(pubTime);
                holder.tvDetails.setText(details7);
                break;
        }//switch



    }

//    private Boolean isSelected(List<ClaimItems> claimItemsList) {
//        for(int i=0;i<claimItemsList.size();i++){
//            if(claimItemsList.get(i).getClaimFlag()!=null){
//                claimPosition = i;
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public int getItemCount() {
        return dataListMsgOrdersBean.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        RelativeLayout relItem;
        TextView tvTime,tvDetails;
        ImageView ivPubUser;

        public MyViewHolder(View view) {
            super(view);
            tvDetails = (TextView) view.findViewById(R.id.tv_details);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            ivPubUser = (ImageView) view.findViewById(R.id.iv_pubUser);
            relItem = (RelativeLayout) view.findViewById(R.id.rel_item);
            relItem.setOnClickListener(this);
            relItem.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rel_item:
                    MyLog.i("onClick"+getLayoutPosition()+"---"+dataListMsgOrdersBean.get(getLayoutPosition()).getOrderContent());
                    MsgOrdersBean msgOrdersBean = dataListMsgOrdersBean.get(getLayoutPosition());
                    MyLog.i("msgOrdersBean.getTextFlag()"+msgOrdersBean.getClaimFlag());
                    switch (msgOrdersBean.getClaimFlag()){
//            0. 微客消息通知， 即你被 别人确认 选中接单
                        case 0:
                            Intent intent0 = new Intent(context, WeikersMsgActivity.class);
//                            intent0.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            context.startActivity(intent0);

                            break;

                        // 1.有认领人数的，没有确定认领人的；
                        case 1:
                            MyLog.v("111111");
                            Intent intent = new Intent(context, OrdersSelectActivity.class);
                            intent.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            context.startActivity(intent);
                            break;
                        // 2.有确定认领人的，任务正在进行中的；
                        case 2:
                            MyLog.i("kind2");
                            Intent intent2 = new Intent(context, OrdersDoingShowActivity.class);
                            intent2.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            intent2.putExtra("stateInfo","任务正在进行中");
                            context.startActivity(intent2);
                            break;
                        // 3.有确定认领人的，任务已经完成的；
                        case 3:

                            Intent intent3 = new Intent(context, OrdersDoingShowActivity.class);
                            intent3.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            intent3.putExtra("stateInfo","任务已完成");
                            context.startActivity(intent3);
                            break;
                        case 4:
                            Intent intent4 = new Intent(context, OrdersDoingShowActivity.class);
                            intent4.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            intent4.putExtra("stateInfo","任务已完成");
                            context.startActivity(intent4);
                            break;
                        case 5:
                            Intent intent5 = new Intent(context, OrdersEvaluateShowActivity.class);
//                            intent5.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
//                            intent5.putExtra("stateInfo",false);
                            context.startActivity(intent5);
                            break;
//            6. 有人选择了你， 接单任务
                        case 6:
                            Intent intent6 = new Intent(context, OrdersDoingShowActivity.class);
                            intent6.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            intent6.putExtra("stateInfo","任务正在进行中");
                            intent6.putExtra("taskDoing",true);
                            context.startActivity(intent6);
                            break;
//            7.你完成了别人 指定的任务/
                        case 7:
                            Intent intent7 = new Intent(context, OrdersDoingShowActivity.class);
                            intent7.putExtra("fromMsgOrdersBeanAdapter", msgOrdersBean);
                            intent7.putExtra("stateInfo","任务已完成");
                            context.startActivity(intent7);
                            break;
                    }//switch getClaimFlag

                    break;
            }
        }
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.rel_item:
                    MyLog.i("onLongClick");
                    ItemLongOnClickDialog.Builder builder = new ItemLongOnClickDialog.Builder(context);
                    builder.setMessage1("置顶此消息", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("Test","stick onclik"+"--which-"+which);
                            dialog.dismiss();
                        }
                    });

                    builder.setMessage2("删除此消息", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.i("Test","delete onclik"+"--which-"+which);
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();
            }
            return true;
        }
    }




}

