package com.team.witkers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.team.witkers.R;
import com.team.witkers.bean.TendItems;
import com.team.witkers.utils.TimeUtils2;
import com.team.witkers.views.NineGridImageView;
import com.team.witkers.views.NineGridImageViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hys on 2016/8/9.
 */
public class TendencyAdapter extends RecyclerView.Adapter<TendencyAdapter.TendcyViewHolder>{

    private Context context;
    private List<TendItems> dataList;
    private onRecyclerViewItemClickListener itemClickListener = null;
    private onItemCommentClickListener itemCommentClickListener=null;

    private onItemLikeClickListener itemLikeClickListener=null;
    private  onItemRoundHeadClickListener itemRoundHeadClickListener=null;
    private onItemRlHeadClickListener itemRlHeadClickListener=null;

    public TendencyAdapter(Context context, List<TendItems> dataList) {
//        MyLog.i("dataList_0_name_ "+dataList.get(0).getFriendName() );
        this.dataList = dataList;
        this.context = context;

    }



    @Override
    public TendcyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TendcyViewHolder holder = new TendcyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.recyclerview_tendency_item, parent,
                false));
        return holder;

    }


    @Override
    public void onBindViewHolder(final TendcyViewHolder holder, final int position) {

        TendItems tend = new TendItems();
        tend = dataList.get(position);

//        MyLog.i("onBind_ "+tend.getCreatedAt());
        if(tend.getCreatedAt()!=null) {
            String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp( TimeUtils2.stringToLong(tend.getCreatedAt(), TimeUtils2.FORMAT_DATE_TIME_SECOND) );
            holder.tv_tendPubtime.setText(pubTime);
        }
        holder.tv_tendName.setText(tend.getFriendName());
        holder.tv_tendPubcontent.setText(tend.getContent());
        holder.tv_commentNum.setText("" + tend.getCommentNum());
        holder.tv_likeNum.setText("" + tend.getLikeNum());
//        isLike=tend.isLike();


        if(!tend.isLike()) {
//            MyLog.d("tendFriendName_ "+tend.getFriendName());
//            MyLog.i("tendLikeName_ "+tend.getLikeName());
            holder.iv_like.setImageResource(R.drawable.ic_tendency_likew1);
        }
        else  //若该用户点赞，则为红
            holder.iv_like.setImageResource(R.drawable.ic_tendency_liker);

//        MyLog.e("tendHeadUrl_ "+tend.getPubUser().getHeadUrl());
        Glide.with(context).load(tend.getPubUser().getHeadUrl()).into(holder.roundIv_tendency_head);


        // 设置tend对象 的 tag，以便获取该对象
//        holder.ll_topview.setTag(tend);
        holder.ll_topview.setTag(position);
        holder.ll_comment.setTag(tend);
        holder.ll_like.setTag(position);
        holder.rl_head.setTag(position);
//        holder.roundIv_tendency_head.setTag(position);



        if (tend.getPicUrlList() != null && !tend.getPicUrlList().get(0).equals("")) {
            holder.ninegrid.setVisibility(View.VISIBLE);
            holder.ninegrid.setImagesData(tend.getPicUrlList());
        } else {
            holder.ninegrid.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.iv_like:
//                holder.iv_like.setVisibility(View.GONE);
//                break;
//        }
//    }


    class TendcyViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        LinearLayout ll_repost, ll_comment, ll_like, ll_topview;
        ImageView iv_repost, iv_comment, iv_like;
        RoundedImageView roundIv_tendency_head;
        TextView tv_tendName, tv_tendPubtime, tv_commentNum, tv_likeNum, tv_tendPubcontent;
        //        ImageView photo1,photo2,photo3;
        NineGridImageView ninegrid;
        private boolean isLike;//第一次和第二次点赞，区分
        RelativeLayout rl_head;

        private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String s) {
                Glide.with(context)
                        .load(s)
                        .placeholder(R.drawable.ic_default_null_gray)
                        .into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            protected void onItemImageClick(Context context, int index, List<String> list) {

                    MyLog.i("image--->"+index+"---"+list.get(index));
                new ImageViewer.Builder(context, (ArrayList<String>) list)
                        .setStartPosition(index)
                        .show();

                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
            }

        };


        public TendcyViewHolder(View v) {
            super(v);
            iv_repost = (ImageButton) v.findViewById(R.id.ib_repost);
            iv_comment = (ImageView) v.findViewById(R.id.iv_comment);
            iv_like = (ImageView) v.findViewById(R.id.iv_like);

            ll_topview = (LinearLayout) v.findViewById(R.id.ll_topview);
            ll_repost = (LinearLayout) v.findViewById(R.id.ll_repost);
            ll_comment = (LinearLayout) v.findViewById(R.id.ll_comment);
            ll_like = (LinearLayout) v.findViewById(R.id.ll_like);

            rl_head= (RelativeLayout) v.findViewById(R.id.rl_head);
            roundIv_tendency_head = (RoundedImageView) v.findViewById(R.id.roundIv_tendency_head);
            tv_tendName = (TextView) v.findViewById(R.id.tv_tendName);
            tv_tendPubtime = (TextView) v.findViewById(R.id.tv_tendPubtime);
            tv_commentNum = (TextView) v.findViewById(R.id.tv_commentNum);
            tv_likeNum = (TextView) v.findViewById(R.id.tv_likeNum);
            tv_tendPubcontent = (TextView) v.findViewById(R.id.tv_tendPubcontent);
//            photo1= (ImageView) v.findViewById(R.id.photo1);
//            photo2= (ImageView) v.findViewById(R.id.photo2);
//            photo3= (ImageView) v.findViewById(R.id.photo3);
            ninegrid = (NineGridImageView) v.findViewById(R.id.ninegrid);
            ninegrid.setAdapter(mAdapter);

            ll_topview.setOnClickListener(this);
            ll_like.setOnClickListener(this);
            ll_comment.setOnClickListener(this);
            rl_head.setOnClickListener(this);
//            roundIv_tendency_head.setOnClickListener(this);


//            ll_like.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    MyToast.showToast(context,"like ");
//                    MyLog.d("like click");
//                }
//            });
//            ll_comment.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    MyToast.showToast(context,"comment ");
//                    MyLog.d("comment click");
//                }
//            });

        }//TendcyViewHolder_ constructor

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_topview:
//                    MyLog.d("topview click");
                    if(itemClickListener!=null){
//                        MyLog.d("topview click not null");
                        itemClickListener.onItemClick(view,(int)view.getTag());
                    }
                    break;
                case R.id.ll_like:
                    if(itemLikeClickListener!=null)
                        itemLikeClickListener.onItemLikeClick(view,(int)view.getTag());
                    break;
                case R.id.ll_comment:
                    MyLog.d("comment click");
                    if(itemCommentClickListener!=null){
                        itemCommentClickListener.onItemCommentClick(view,(TendItems)view.getTag());
                    }
                    break;
                case R.id.rl_head:
                    MyLog.d("tendency rl_head");
                    if(itemRlHeadClickListener!=null){
                        MyLog.d("tendency rl_head not null");
                        itemRlHeadClickListener.onItemRlHeadClickListener(view,(int)view.getTag());
                    }else{
                        MyLog.d("tendency rl_head 22 null");
                    }
                    break;
            }//switch
        }
    }//TendencyViewHolder

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
//        MyLog.v("设置listener");
        this.itemClickListener = listener;
        //  Log.d("ddd", itemClickListener.toString());
    }

    public void setOnItemCommentClickListener(onItemCommentClickListener listener){
        this.itemCommentClickListener=listener;
    }

    public  interface onRecyclerViewItemClickListener {
        void onItemClick(View v,int position);
    }

    public interface onItemCommentClickListener{
        void onItemCommentClick(View v,TendItems tendTag);
    }

    public interface onItemLikeClickListener{
        void onItemLikeClick(View v,int itemPosition);
    }
    public void setOnItemLikeClickListener(onItemLikeClickListener listener){
        this.itemLikeClickListener=listener;
    }

    //设置头像点击事件
    public interface onItemRoundHeadClickListener{
        void onItemRoundHeadClickListener(View v,int position);
    }
    public void setOnItemRoundHeadClickListener(onItemRoundHeadClickListener listener){
        this.itemRoundHeadClickListener=listener;
    }

    //设置头像rl布局点击事件    //设置头像点击事件
    public interface onItemRlHeadClickListener{
        void onItemRlHeadClickListener(View v,int position);
    }
    public void setOnItemRlHeadClickListener(onItemRlHeadClickListener listener){
        this.itemRlHeadClickListener=listener;
    }

}//TendencyAdapter

