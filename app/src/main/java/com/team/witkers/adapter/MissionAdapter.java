package com.team.witkers.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.FindMissionPageActivity;
import com.team.witkers.activity.homeitem.ClaimTaskActivity2;
import com.team.witkers.activity.homeitem.NoUnderlineClickableSpan;
import com.team.witkers.activity.homeitem.PersonalHomePageActivity2;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.ChooseNotify;
import com.team.witkers.utils.DigitsUtils;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.liteasysuits.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by hys on 2016/7/30.
 */
// 注意泛型添加自定义ViewHoler   RecyclerView.Adapter<TakeOutMissionAdapter.TakeOutViewHolder>
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.TakeOutViewHolder>
{
    private Context context;
    private List<Mission> dataList;
    private static final int REQUEST_CODE =1;//响应码
    private List<Mission> missonList = new ArrayList<>();


    public MissionAdapter(Context context, List<Mission> dataList){
        this.context=context;
        this.dataList=dataList;
    }

    @Override
    public TakeOutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_mission_item_2, parent,false);
        TakeOutViewHolder holder = new TakeOutViewHolder(view);
        return holder;
    }


/*
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    int Test;
    @Override
    public void onClick(View v) {
        if (mOnItemClickLitener != null){
            mOnItemClickLitener.onItemClick(v,Test);
        }
    }*/

    @Override
    public void onBindViewHolder(final TakeOutViewHolder holder, final int position) {

        Mission mission=new Mission();
        mission=dataList.get(position);
//    MyLog.e("MissionAdapter_ ");
        //TODO 设置头像
        if(mission.getPubUser().getUsername()!=null){
            String headUrl=mission.getPubUser().getHeadUrl();

//         MyLog.e("userName_ "+mission.getPubUser().getUsername());
//            MyLog.d("headUrl__"+headUrl);

            Glide.with(context)
                    .load(headUrl)
                    .into(holder.round_head);
        }else{
//            MyLog.d("headUrl null");
            Glide.with(context)
                    .load(R.drawable.default_round2)
                    .into(holder.round_head);
        }

        //TODO 设置头像点击事件
        holder.round_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Test","clicked"+position);
                if(BmobUser.getCurrentUser(MyUser.class)==null){
                    MyToast.showToast(context,"亲，你还没登陆哦");
                    return;
                }
                Intent intent = new Intent(context, PersonalHomePageActivity2.class);
                intent.putExtra("fromTakeOutMissionAdapterTV",dataList.get(position).getPubUser());
                context.startActivity(intent);
            }
        });

        holder.tv_takeoutfrm_name.setText(mission.getPubUserName());

        holder.tv_takeoutfrm_time.setText(mission.getFinishTime()+"");
        //通过工具类将浮点数转化为所需要的小数点后两位
        holder.tv_takeoutfrm_price.setText(DigitsUtils.setDigitsPoint(mission.getCharges()));
        holder.tv_takeoutfrm_address.setText(mission.getAddress());

        //TODO 设置标签链接
        final String infoTemp = mission.getInfo();
//        MyLog.i("infoTemp-->"+infoTemp);
        final int index1 = infoTemp.indexOf("#");
        //如果没有这个字符就返回-1
//        MyLog.v("index1 _ "+index1);


        if (index1!=-1) {
            int index2 = infoTemp.indexOf("#", index1 + 1);

//            MyLog.v("index2 _ "+index2);
//            MyLog.v("new String_ "+builder.toString());
            SpannableString spannableString = null;
            if (index2 != -1) {

                final StringBuilder builder=new StringBuilder(infoTemp);
                if(index1!=0){
                    builder.insert(index1," ");
                    index2=index2+1;
                }else{
                    builder.insert(index2+1," ");
                }
                spannableString = new SpannableString(builder);

                //NoUnderlineClickableSpan 设置为不要下划线的类
                final int finalIndex = index2;
                spannableString.setSpan(new NoUnderlineClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                        MyToast.showToast(context,"onclick lable");
                        //TODO 这里修改跳转的目标activity


                        String labelStr = infoTemp.substring(index1+1, finalIndex);
//                        StringBuilder builder2=new StringBuilder(labelStr);
                       labelStr= labelStr.replace("#","");
                        MyLog.i("labelStr_"+labelStr);

                        // 查找相关标签
                        BmobQuery<Mission> query = new BmobQuery<Mission>();
                        query.include("pubUser");
                        query.addWhereEqualTo("category",labelStr);
//                        MyLog.v("labelStr_ "+labelStr);
                        query.findObjects(new FindListener<Mission>() {

                            @Override
                            public void done(List<Mission> list, BmobException e) {
                                if(e==null){
                                    if(list.size()==0){
                                        MyLog.i("ii没有查找到相关任务");
                                    }
                                    missonList = list;
                                    Intent intent2 = new Intent(context, FindMissionPageActivity.class);
                                    intent2.putExtra("fromTakeOutMissionAdapterTV",(Serializable)missonList);
                                    intent2.putExtra("fromFindMissoionLabel",missonList.get(0).getCategory());
                                    context.startActivity(intent2);


                                }else{
                                    MyLog.i("contains error"+e);
                                }
                            }
                        });

//                        context.startActivity(intent);

//                        Intent intent = new Intent(context, SearchActivity.class);
//                        String lableStr = infoTemp.substring(index1+1, index2);
//                        intent.putExtra("ToHotLableDetails",lableStr);
//                        context.startActivity(intent);
                    }//onCLick



                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.rgb(74,125,174));       //设置文件颜色
                    }
                }, index1, index2 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
/////////////  其他文字 部分
                if(index1!=0){//即标签在中间，设置 标签前面的非标签文字
                    spannableString.setSpan(new NoUnderlineClickableSpan(){
                        //  前端绿色文字
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(context.getResources().getColor(R.color.black_light));       //设置文件颜色
                        }

                        @Override
                        public void onClick(View widget) {
//                            MyLog.d("前端文字");
                            Mission mission=dataList.get(position);
                            Intent intent = new Intent(context, ClaimTaskActivity2.class);
                            intent.putExtra("fromTakeOutMissionAdapterLIN",mission);
                            context.startActivity(intent);
                        }
                    },0,index1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

//                MyLog.v("spannableString_length_ "+spannableString.length());
                if(index2!=spannableString.length()-2){// 不相等时， 即结束标签不在最后， 要设置后端文字
                    spannableString.setSpan(new NoUnderlineClickableSpan(){
                        // 后端红色文字
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(context.getResources().getColor(R.color.black_light));       //设置文件颜色
                        }


                        @Override
                        public void onClick(View widget) {
//                            MyLog.d("后端文字");
                            Mission mission=dataList.get(position);
                            Intent intent = new Intent(context, ClaimTaskActivity2.class);
                            intent.putExtra("fromTakeOutMissionAdapterLIN",mission);
                            context.startActivity(intent);
                        }
                    },index2+1,spannableString.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }


                holder.tv_takeoutfrm_mission.setText(spannableString);
            }else{
                holder.tv_takeoutfrm_mission.setText(infoTemp);
            }
            holder.tv_takeoutfrm_mission.setMovementMethod(LinkMovementMethod.getInstance());
        }else {//外if
            holder.tv_takeoutfrm_mission.setText(infoTemp);
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class TakeOutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RoundedImageView round_head;
        TextView tv_takeoutfrm_name,tv_takeoutfrm_time,tv_takeoutfrm_price,
                tv_takeoutfrm_address,tv_takeoutfrm_mission;
        LinearLayout itemView;

        public TakeOutViewHolder(View view)
        {
            super(view);
            round_head= (RoundedImageView) view.findViewById(R.id.round_head);
            tv_takeoutfrm_name= (TextView) view.findViewById(R.id.tv_takeoutfrm_name);
            tv_takeoutfrm_time= (TextView) view.findViewById(R.id.tv_takeoutfrm_time);
            tv_takeoutfrm_price= (TextView) view.findViewById(R.id.tv_takeoutfrm_price);
            tv_takeoutfrm_address= (TextView) view.findViewById(R.id.tv_takeoutfrm_address);
            tv_takeoutfrm_mission= (TextView) view.findViewById(R.id.tv_takeoutfrm_mission);
            itemView = (LinearLayout)view .findViewById(R.id.ll_missionState_itemView);
            itemView.setOnClickListener(this);
        }


        //每个itemView点击事件
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_missionState_itemView:
                    MyLog.i("Lin-clicked-->"+getLayoutPosition());
                    if(MyApplication.mUser==null)
                    {
                        MyToast.showToast(context,"亲，你还没登录呢！");
                        return;
                    }
                    Mission mission=dataList.get(getLayoutPosition());
                    Intent intent = new Intent(context, ClaimTaskActivity2.class);
                    intent.putExtra("fromTakeOutMissionAdapterLIN",mission);
                    context.startActivity(intent);
                    break;
            }
        }
    }//TakoutViewHoler_cls

}//TakeOutAdapter_cls
