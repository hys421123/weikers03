package com.team.witkers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.witkers.R;

/**
 * 拍照适配器
 * Created by zcf on 2016/4/17.
 */
public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public PhotoAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item_photo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case 0:
                viewHolder.textView.setText(context.getString(R.string.take_photo));
                viewHolder.imageView.setImageResource(R.drawable.ic_photo_take);
                break;
            case 1:
                viewHolder.textView.setText(context.getString(R.string.album));
                viewHolder.imageView.setImageResource(R.drawable.ic_photo_album);
                break;
            default:

                break;
        }

        return view;
    }


    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
