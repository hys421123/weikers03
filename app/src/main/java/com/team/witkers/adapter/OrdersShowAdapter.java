package com.team.witkers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.homeitem.PersonalHomePageActivity2;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by jin on 2016/9/14.
 */
public class OrdersShowAdapter extends RecyclerView.Adapter<OrdersShowAdapter.MyViewHolder> {
    private List<ClaimItems> claimItemsList;
    private Context context;
    ClaimItems claimItems;
    private int mSelectedItem = -1;

    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data,int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public OrdersShowAdapter(Context context, List<ClaimItems> dataList){
        this.context = context;
        this.claimItemsList = dataList;
    }

    @Override
    public OrdersShowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_orders_show_item, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrdersShowAdapter.MyViewHolder holder, final int position) {

        holder.cbSelect.setChecked(position == mSelectedItem);

        claimItems = claimItemsList.get(position);

        String headUrl = claimItems.getClaimHeadUrl();
        final String name = claimItems.getClaimName();
        float price = claimItems.getClaimMoney();

        holder.itemView.setTag(name);//设置Tag，传递到Activity中去
        if(headUrl==null||headUrl.equals("")){
            Glide.with(context).load(R.drawable.default_head).into(holder.ivHead);
        }else{
            Glide.with(context).load(headUrl).into(holder.ivHead);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前点击的位置
                mSelectedItem = position;
//                holder.cbSelect.setChecked(true);
                notifyItemRangeChanged(0, claimItemsList.size());
                MyLog.i("onClick--itemView-->"+position);
                mOnItemClickListener.onItemClick(holder.itemView, (String) holder.itemView.getTag(), position);
            }
        });

/*        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedItem = position;
                notifyItemRangeChanged(0, claimItemsList.size());
                MyLog.i("onClick--CheckBox-->"+position);

                mOnItemClickListener.onItemClick(holder.cbSelect, (String) holder.cbSelect.getTag(), position);
            }
        });*/

        holder.tvPrice.setText("￥"+price+"元");
        holder.tvName.setText(name);
        holder.tvOrdersNum.setText("月完成--单");

        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i("onclick---"+position);queryUser(name);

            }
        });
    }

    private void queryUser(String userName){
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("username", userName);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e==null){
                    MyUser myUser = list.get(0);
                    Intent intent = new Intent(context, PersonalHomePageActivity2.class);
                    intent.putExtra("fromTakeOutMissionAdapterTV",myUser);
                    context.startActivity(intent);
                }else{
                    MyLog.i("erro");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return claimItemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        TextView tvName,tvOrdersNum,tvPrice;
        ImageView ivHead;
        CheckBox cbSelect;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvOrdersNum = (TextView) view.findViewById(R.id.tv_ordersNum);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            ivHead = (ImageView) view.findViewById(R.id.iv_takerIcon);
            cbSelect = (CheckBox) view .findViewById(R.id.cb_select);
        }
    }




}
