package com.team.witkers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.team.witkers.activity.homeitem.TaskDetailsActivity2;
import com.team.witkers.bean.Mission;
import com.team.witkers.utils.MyToast;

import java.util.List;

/**
 * Created by hys on 2016/11/9.
 */

public class MissionStateAdapter extends RecyclerView.Adapter<MissionStateAdapter.MissionStateViewHolder> {
    private Context context;
    private List<Mission> dataList;

   public  MissionStateAdapter(Context context, List<Mission> dataList){
        this.context=context;
        this.dataList=dataList;
    }

    @Override
    public MissionStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_mission_state_item, parent,false);
        MissionStateAdapter.MissionStateViewHolder holder = new MissionStateAdapter.MissionStateViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MissionStateViewHolder holder, int position) {
        Mission mission=new Mission();
        mission=dataList.get(position);

        //TODO 设置头像
        if(mission.getPubUserHeadUrl()!=null){
            String headUrl=dataList.get(position).getPubUserHeadUrl();
//            MyLog.d("headUrl__"+headUrl);
            Glide.with(context)
                    .load(headUrl)
                    .into(holder.round_head);
        }else{
            Glide.with(context)
                    .load(R.drawable.default_head)
                    .into(holder.round_head);
        }

        holder.tv_missionstate_name.setText(mission.getPubUserName());

        // TODO: 2016/11/9  设置任务状态的
        String status="";
        //对认领者来说
        if(mission.getChooseClaimant()!=null&&mission.getChooseClaimant().getClaimName().equals(MyApplication.mUser.getUsername())&&!mission.getFinished()){
            status="正在进行中";
        }
        //对于发布者来说
        if(mission.getChooseClaimant()!=null&&!mission.getFinished()&&(mission.getPubUserName().equals(MyApplication.mUser.getUsername()))  ){
            MyLog.v("发布者");
            status="正在进行中";
        }
        if(mission.getFinished()){
            status="已结束";
        }
        holder.tv_missionstate_status.setText(status);

        holder.tv_missionstate_details.setText(mission.getInfo());
        holder.tv_missionstate_price.setText("¥"+mission.getCharges()+"");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MissionStateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RoundedImageView round_head;
        TextView tv_missionstate_name,tv_missionstate_status,tv_missionstate_details,
                tv_missionstate_price;
        LinearLayout itemView;

        public MissionStateViewHolder(View view)
        {
            super(view);
            round_head= (RoundedImageView) view.findViewById(R.id.round_head);
            tv_missionstate_name= (TextView) view.findViewById(R.id.tv_missionstate_name);
            tv_missionstate_status= (TextView) view.findViewById(R.id.tv_missionstate_status);
            tv_missionstate_details= (TextView) view.findViewById(R.id.tv_missionstate_details);
            tv_missionstate_price= (TextView) view.findViewById(R.id.tv_missionstate_price);
            itemView = (LinearLayout)view .findViewById(R.id.lin_itemView);
            itemView.setOnClickListener(this);
        }


        //每个itemView点击事件
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lin_itemView:
//                    MyLog.i("Lin-clicked-->"+getLayoutPosition());
//                    if(MyApplication.mUser==null)
//                    {
//                        MyToast.showToast(context,"亲，你还没登录呢！");
//                        return;
//                    }
//                    Mission mission=dataList.get(getLayoutPosition());
//                    Intent intent = new Intent(context, TaskDetailsActivity2.class);
//                    intent.putExtra("fromTakeOutMissionAdapterLIN",mission);
//                    context.startActivity(intent);
                    break;
            }
        }
    }//MIssionStateViewHoler_cls
}
