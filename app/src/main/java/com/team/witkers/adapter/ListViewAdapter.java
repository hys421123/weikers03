package com.team.witkers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.witkers.R;
import com.team.witkers.bean.LocationBean;

import java.util.List;

/**
 * Created by jin on 2016/9/29.
 */
public class ListViewAdapter extends BaseAdapter {

    List<LocationBean> locationBeanList;
    Context context;
    private LayoutInflater mInflater;

    public ListViewAdapter(Context context, List<LocationBean> locationBeanList){
        this.context = context;
        this.locationBeanList = locationBeanList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return locationBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder = new ViewHolder(context,parent,R.layout.location_itemview,position);
//        convert(viewHolder, getItem(position));
//        viewHolder.setBackgroundRes(R.id.iv_location,locationBeanList.get(position).getIconId());
//        viewHolder.setText(R.id.tv_title,locationBeanList.get(position).getTitle());
//        viewHolder.setText(R.id.tv_content,locationBeanList.get(position).getContent());

        ViewHolder2 holder = null;
        if (convertView == null) {
            holder=new ViewHolder2();
            convertView = mInflater.inflate(R.layout.location_itemview, null);
            holder.iv_location = (ImageView)convertView.findViewById(R.id.iv_location);
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView)convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder2)convertView.getTag();
        }
        holder.iv_location.setBackgroundResource(locationBeanList.get(position).getIconId());
        holder.tv_title.setText(locationBeanList.get(position).getTitle());
        holder.tv_content.setText(locationBeanList.get(position).getContent());
        return convertView;
    }

    public final class ViewHolder2{
        public ImageView iv_location;
        public TextView tv_title;
        public TextView tv_content;
    }

}
