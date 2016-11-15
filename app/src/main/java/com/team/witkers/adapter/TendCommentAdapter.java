package com.team.witkers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.R;
import com.team.witkers.bean.TendComments;
import com.team.witkers.utils.TimeUtils2;
import com.team.witkers.views.NineGridImageView;
import com.team.witkers.views.NineGridImageViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hys on 2016/8/18.
 */
public class TendCommentAdapter extends RecyclerView.Adapter<TendCommentAdapter.CommentViewHolder> {

    private Context context;
    private List<TendComments> dataList = new ArrayList<>();

    public TendCommentAdapter(Context context, List<TendComments> dataList) {
        this.context=context;
        this.dataList=dataList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommentViewHolder holder = new CommentViewHolder(LayoutInflater.from(
                context).inflate(R.layout.recyclerview_tend_coment_item, parent, false));
        return holder;
    }

//    public void add(TendComments comment,  int position) {
//        dataList.add(position, comment);
//        notifyItemInserted(position);
//    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        TendComments subTend = dataList.get(position);
//            MyLog.d("commentUsr_ "+commentUsrName);

        Glide.with(context).load(subTend.getCommentUserHead()).error(R.drawable.ic_default_null_gray).into(holder.roundIv_comment_head);
        holder.tv_commentName.setText(subTend.getCommentUserName());

        String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(TimeUtils2.stringToLong(subTend.getCommentPubtime(), TimeUtils2.FORMAT_DATE_TIME_SECOND));
        holder.tv_commentPubtime.setText(pubTime);

        holder.tv_comment_content.setText(subTend.getCommentContent());
    }

    @Override
    public int getItemCount() {
        if(dataList==null)
            return 0;
//        MyLog.v("dataSize_ "+dataList.size());
        return dataList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView roundIv_comment_head, roundIv_comment_tophead;
        private TextView tv_commentName, tv_commentPubtime, tv_comment_content;
        private TextView tv_comment_topName, tv_comment_topPubtime, tv_comment__topPubcontent, tv_comment_topCommentNum, tv_comment_toplikeNum;
        private NineGridImageView ninegrid, comment_topninegrid;
        private NineGridImageViewAdapter<String> mAdapterNine = new NineGridImageViewAdapter<String>() {
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
                Toast.makeText(context, "image position is " + index, Toast.LENGTH_SHORT).show();
            }
        };

        public CommentViewHolder(View itemView) {
            super(itemView);
            roundIv_comment_head = (RoundedImageView) itemView.findViewById(R.id.roundIv_comment_head);
            tv_commentName = (TextView) itemView.findViewById(R.id.tv_commentName);
            tv_commentPubtime = (TextView) itemView.findViewById(R.id.tv_commentPubtime);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);

        }

    }//CommentViewHolder
}//TendCommentAdapter2
