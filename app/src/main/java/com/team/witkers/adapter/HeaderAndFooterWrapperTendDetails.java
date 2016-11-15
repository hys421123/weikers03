package com.team.witkers.adapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
 * Created by hys on 2016/8/18.
 */
public class HeaderAndFooterWrapperTendDetails<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int BASE_ITEM_TYPE_HEADER = 100;
    private static final int BASE_ITEM_TYPE_FOOTER = 200;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mInnerAdapter;
    private TendItems data;
    private Context context;
    private onItemCommentClickListener itemCommentClickListener=null;
    private onItemLikeClickListener itemLikeClickListener=null;

    public HeaderAndFooterWrapperTendDetails(Context context, TendItems data, RecyclerView.Adapter adapter)
    {
        this.context=context;
        this.data=data;
        mInnerAdapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {

        if (isHeaderViewPos(position))
        {
//            MyLog.i("itemViewType_ "+mHeaderViews.keyAt(position));
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position))
        {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }

        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }
    private boolean isHeaderViewPos(int position)
    {
        return position < getHeadersCount();
    }
    private boolean isFooterViewPos(int position)
    {
        return position >= getHeadersCount() + getRealItemCount();
    }

    private int getRealItemCount()
    {
        return mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemCount() {
//        MyLog.i("itemCount_ "+mInnerAdapter.getItemCount());
        return getHeadersCount()+mInnerAdapter.getItemCount()+getFootersCount();
    }
    public void addHeaderView(View view)
    {
//        MyLog.i("headerViews_size_ "+mHeaderViews.size());
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }
    public void addFootView(View view)
    {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }


    public int getHeadersCount()
    {
        return mHeaderViews.size();
    }
    public int getFootersCount()
    {
        return mFootViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderViews.get(viewType) != null) {
//            MyLog.i("headerview_viewType_ "+viewType);
            MyViewHolder holder = new MyViewHolder(mHeaderViews.get(viewType),viewType);
            return holder;
        }else if (mFootViews.get(viewType) != null)
        {
            MyViewHolder holder = new MyViewHolder(mFootViews.get(viewType),viewType) ;
            return holder;
        }
//        MyLog.i("else_viewType_ "+viewType);
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        ((MyViewHolder)holder).tvName.setText("张三");
        if (isHeaderViewPos(position))
        {
            ((MyViewHolder)holder).tvName.setText(data.getFriendName());
            ((MyViewHolder)holder).tvContent.setText(data.getContent());

            String timeStr="";
            if(data.getCreatedAt()==null)
                timeStr=data.getCreateTime();
            else
                timeStr=data.getCreatedAt();
//            MyLog.v("HeadFootDetails_before pubTime");
            String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(TimeUtils2
                    .stringToLong(timeStr, TimeUtils2.FORMAT_DATE_TIME_SECOND));
//            MyLog.i("time_ "+data.getCreatedAt());
//            String pubTime="hahe";
//            MyLog.v("after pubTime");
            ((MyViewHolder)holder).tvPubTime.setText(pubTime);
            ((MyViewHolder)holder).tvCommentNum.setText(data.getCommentNum()+"");
            ((MyViewHolder)holder).tvLikeNum.setText(data.getLikeNum()+"");
            if(data.isLike())//true为红
                ((MyViewHolder) holder).iv_like .setImageResource(R.drawable.ic_tendency_liker);
            else//false为白
                ((MyViewHolder) holder).iv_like.setImageResource(R.drawable.ic_tendency_likew1);


            Glide.with(context).load(data.getFriendHeadUrl()).error(R.drawable.ic_default_null_gray)
                    .into(((MyViewHolder)holder).rIv);

            List<String> list2=data.getPicUrlList();
            if (list2 != null && !list2.get(0).equals("")) {
                ((MyViewHolder)holder).nineImg.setVisibility(View.VISIBLE);
                ((MyViewHolder)holder).nineImg.setImagesData(list2);
            } else {
                ((MyViewHolder)holder).nineImg.setVisibility(View.GONE);
            }

/*

            final int[] likeNum = {data.getLikeNum()};
            final TendItems finalTend = data;
            ((MyViewHolder)holder).isLike=data.isLike();
//        MyLog.v("before_click_isLike_position "+holder.isLike+"(( "+position

            ((MyViewHolder)holder).ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(! ((MyViewHolder)holder).isLike) {
//                    MyLog.v("red_isLike_position "+ ((MyViewHolder)holder).isLike+"(( "+position);
                         ((MyViewHolder)holder).iv_like.setImageResource(R.drawable.ic_tendency_liker);
//                     ((MyViewHolder)holder).iv_like.setImageDrawable(context.getResources().getDrawable());
                        likeNum[0]++;
                         ((MyViewHolder)holder).tvLikeNum.setText(likeNum[0]+"");
                    }else{
                         ((MyViewHolder)holder).iv_like.setImageResource(R.drawable.ic_tendency_likew);
                        likeNum[0]--;
                         ((MyViewHolder)holder).tvLikeNum.setText(likeNum[0]+"");
//                     ((MyViewHolder)holder).iv_like.setImageDrawable(context.getResources().getDrawable());
                    }
                     ((MyViewHolder)holder).isLike=! ((MyViewHolder)holder).isLike;
                    
                }//onClick
            });//setOnClick
*/
            return;
        }//isHeaderView

        if (isFooterViewPos(position))
        {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }


    class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout ll_repost, ll_comment, ll_like, ll_topview;
        TextView tvName,tvContent,tvPubTime,tvCommentNum,tvLikeNum;
        ImageView iv_like;
        RoundedImageView rIv;
        NineGridImageView nineImg;
        boolean isLike;
        NineGridImageViewAdapter<String> nineAdapter = new NineGridImageViewAdapter<String>() {
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

                new ImageViewer.Builder(context, (ArrayList<String>) list)
                        .setStartPosition(index)
                        .show();
                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
            }
        };

        public MyViewHolder(View itemView,int viewType) {
            super(itemView);
            if(viewType==BASE_ITEM_TYPE_HEADER) {
                iv_like= (ImageView) itemView.findViewById(R.id.iv_like);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                ll_like= (LinearLayout) itemView.findViewById(R.id.ll_like);
                tvName = (TextView) itemView.findViewById(R.id.tv_comment_topName);
                tvContent = (TextView) itemView.findViewById(R.id.tv_comment_topPubcontent);
                tvPubTime = (TextView) itemView.findViewById(R.id.tv_comment_topPubtime);
                tvCommentNum = (TextView) itemView.findViewById(R.id.tv_comment_topCommentNum);
                tvLikeNum = (TextView) itemView.findViewById(R.id.tv_comment_toplikeNum);

                rIv = (RoundedImageView) itemView.findViewById(R.id.roundIv_comment_tophead);
                nineImg = (NineGridImageView) itemView.findViewById(R.id.comment_topninegrid);
                nineImg.setAdapter(nineAdapter);
                ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemCommentClickListener != null)
                            itemCommentClickListener.onItemCommentClick(view);
                    }
                });//setOnClickListener

                ll_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemLikeClickListener != null)
                            itemLikeClickListener.onItemLikeClick(view);
                    }
                });//seOnclickListener
            }
        }//MyViewHolder


    }//mViewHolder_cls

    public void setOnItemCommentClickListener(onItemCommentClickListener listener){
        this.itemCommentClickListener=listener;
    }
    public interface onItemCommentClickListener{
        void onItemCommentClick(View v);
    }

        public interface onItemLikeClickListener{
        void onItemLikeClick(View v);
    }
    public void setOnItemLikeClickListener(onItemLikeClickListener listener){
        this.itemLikeClickListener=listener;
    }
}//HeaderAndFooterWrapper
