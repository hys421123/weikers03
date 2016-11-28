package com.team.witkers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.team.witkers.R;


import java.io.File;
import java.util.List;

import rx.Observable;

import static android.graphics.Bitmap.CompressFormat.WEBP;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * Created by hys on 2016/8/12.
 */
public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private List<String> dataList;

    public ImageListAdapter(Context context, List<String> list) {
        super(context, R.layout.gridview_img_item_2, list);

        this.context = context;
        this.dataList = list;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.gridview_img_item_2, parent, false);
        }
//        MyLog.i("adapter_getView_list_"+dataList.get(position));
        File imageFile = new File(dataList.get(position));

//        File newFile = new File( context. getCacheDir(), "cropped.jpg");


        Glide.with(context)
                .load(imageFile)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into((ImageView) convertView);        ;

        return convertView;
    }
}
