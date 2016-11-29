package com.team.witkers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.bean.ConcernFans;
import com.team.witkers.bean.Mission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/11/28.
 */

public class ConcernsFansAdapter extends RecyclerView.Adapter< ConcernsFansAdapter.ConcernsFansViewHolder> {

    private Context context;
    private List<ConcernFans> dataList;
    private List<ConcernFans> concernFansList = new ArrayList<>();
    private List<ConcernFans> meConcernsList=null;
    private  boolean isConcerned3;//判断是否 被 本用户关注的 状态


    public ConcernsFansAdapter(Context context, List<ConcernFans> dataList){
        this.context=context;
        this.dataList=dataList;
    }
    public ConcernsFansAdapter(Context context, List<ConcernFans> dataList,List<ConcernFans> meConcernsList){
        this.context=context;
        this.dataList=dataList;
        this.meConcernsList=meConcernsList;
    }

    @Override
    public ConcernsFansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_concernfans_item, parent,false);
        ConcernsFansViewHolder viewHolder=new ConcernsFansViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ConcernsFansViewHolder holder, int position) {
        ConcernFans concernFans=dataList.get(position);

        //TODO 设置头像
        if(concernFans.getHeadUrl()!=null){

            Glide.with(context)
                    .load(concernFans.getHeadUrl())
                    .into(holder.concerns2_head);
        }else{
            Glide.with(context)
                    .load(R.drawable.ic_default_null_gray)
                    .into(holder.concerns2_head);
        }

        // TODO: 2016/11/28   设置名字 简介
        holder.tv_concerns2_name.setText(concernFans.getUserName());
        holder.tv_concerns2_info.setText(concernFans.getInfo());

        // TODO: 2016/11/29  设置 关注图片
        //若关注或粉丝是自己的话，则不出现
         if( concernFans.getUserName().equals(MyApplication.mUser.getUsername()) ){
            holder.iv_isConcerned.setVisibility(View.INVISIBLE);
             return;
        }


//        //   只是 显示自己的粉丝时， 这个list没有设置， 为 null
//        if(meConcernsList==null){
//            MyLog.v("meConcernsList null");
//
//            if(concernFans.getConcerned()){// 若处于互相 关注
//                Glide.with(context)
//                        .load(R.drawable.concerned2)
//                        .into(holder.iv_isConcerned);
//                isConcerned3=true;
//            }else{//不是互相关注
//                Glide.with(context)
//                        .load(R.drawable.unconcerned2)
//                        .into(holder.iv_isConcerned);
//                isConcerned3=false;
//            }
//
//        }else
        if(meConcernsList!=null&&meConcernsList.size()!=0)
        { // meConcernsList not null 显示别人的粉丝、关注的时候
            // 如果 我的关注者 中 含有 他人的粉丝或 关注时，表明 与我互粉的 关联
//            MyLog.v("meConcernsList not null");
            for(int i=0;i<meConcernsList.size();i++){
              MyLog.v("meConcernsList_userName_ "+ meConcernsList.get(i).getUserName());
            }
            MyLog.d("concernFans_userName_"+ concernFans.getUserName() );
            MyLog.e(meConcernsList.contains(concernFans)+"");

            if(meConcernsList.contains(concernFans)){
                Glide.with(context)
                        .load(R.drawable.concerned2)
                        .into(holder.iv_isConcerned);
                isConcerned3=true;
            }else{
                Glide.with(context)
                        .load(R.drawable.unconcerned2)
                        .into(holder.iv_isConcerned);
                isConcerned3=false;
            }
        }// meConcernsList not null 显示别人的粉丝、关注的时候

        // TODO: 2016/11/30  添加 关注的 点击事件

        holder.iv_isConcerned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLog.d("click concerned");
                if(isConcerned3){//若已经互相关注了，  则取消关注
                    Glide.with(context)
                            .load(R.drawable.unconcerned2)
                            .into(holder.iv_isConcerned);
                    isConcerned3=false;
                }else{
                    Glide.with(context)
                            .load(R.drawable.concerned2)
                            .into(holder.iv_isConcerned);
                    isConcerned3=true;
                }//else

            }//onClick
        });


    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ConcernsFansViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView concerns2_head;
        private TextView tv_concerns2_name,   tv_concerns2_info;
        private ImageView iv_isConcerned;
        LinearLayout itemView;

        public ConcernsFansViewHolder(View itemView) {
            super(itemView);
            concerns2_head= (RoundedImageView) itemView.findViewById(R.id.concerns2_head);
            tv_concerns2_name= (TextView) itemView.findViewById(R.id.tv_concerns2_name);
            tv_concerns2_info= (TextView) itemView.findViewById(R.id.tv_concerns2_info);
            iv_isConcerned= (ImageView) itemView.findViewById(R.id.iv_isConcerned);


        }


    }//ViewHolder
}//ConcernsFansAdapter_cls
