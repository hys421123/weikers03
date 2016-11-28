package com.team.witkers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
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

    public ConcernsFansAdapter(Context context, List<ConcernFans> dataList){
        this.context=context;
        this.dataList=dataList;
    }

    @Override
    public ConcernsFansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_concernfans_item, parent,false);
        ConcernsFansViewHolder viewHolder=new ConcernsFansViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConcernsFansViewHolder holder, int position) {
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


    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ConcernsFansViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RoundedImageView concerns2_head;
        private TextView tv_concerns2_name,   tv_concerns2_info;
        LinearLayout itemView;

        public ConcernsFansViewHolder(View itemView) {
            super(itemView);
            concerns2_head= (RoundedImageView) itemView.findViewById(R.id.concerns2_head);
            tv_concerns2_name= (TextView) itemView.findViewById(R.id.tv_concerns2_name);
            tv_concerns2_info= (TextView) itemView.findViewById(R.id.tv_concerns2_info);

        }

        @Override
        public void onClick(View view) {

        }
    }//ViewHolder
}//ConcernsFansAdapter_cls