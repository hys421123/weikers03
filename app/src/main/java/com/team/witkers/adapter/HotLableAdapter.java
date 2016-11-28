package com.team.witkers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team.witkers.R;
import com.team.witkers.bean.Lable;

import java.util.List;

/**
 * Created by jin on 2016/9/23.
 */
public class HotLableAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private List<Lable> lableList;

    public HotLableAdapter(Context context, List<Lable> lableList) {
        this.context = context;
        this.lableList = lableList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lableList.size();
    }

    @Override
    public Object getItem(int position) {
        return lableList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hot_lable_itemview_2, null);
            holder = new ViewHolder();
            holder.tv_hotLable = (TextView)convertView.findViewById(R.id.tv_hotLable);
            holder.iv_lableIcon = (ImageView) convertView.findViewById(R.id.iv_lableIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv_hotLable.setText(lableList.get(position).getLableName());
        String headUrl=lableList.get(position).getHeadUrl();

        if(headUrl != null){
            switch (headUrl){
                case "mission":

                    Glide.with(context).load(R.drawable.img_lable).into(holder.iv_lableIcon);
                    break;
                case "lable":
                    Glide.with(context).load(R.drawable.img_lable).into(holder.iv_lableIcon);
                    break;
                default:
                    Glide.with(context).load(headUrl).into(holder.iv_lableIcon);
                    break;
            }
        }else {
            Glide.with(context).load(R.drawable.default_head).into(holder.iv_lableIcon);
        }
        return convertView;
    }
    public static class ViewHolder {
        public TextView tv_hotLable;
        public ImageView iv_lableIcon;
    }
}







