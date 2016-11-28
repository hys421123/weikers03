package com.team.witkers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.R;
import com.team.witkers.bean.MsgTendBean;
import com.team.witkers.bean.TendComments;
import com.team.witkers.bean.TendItems;
import com.team.witkers.bean.TendLikes;
import com.team.witkers.utils.TimeUtils2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hys on 2016/8/29.
 */
public class MsgTendAdapter extends RecyclerView.Adapter<MsgTendAdapter.MsgTendViewHolder> {

    private Context context;
    private onRecyclerViewItemClickListener itemClickListener = null;
//    private List<TendItems> dataList;
//    private List<TendComments> commentList=new ArrayList<>();
//    private List<TendLikes> likeList=new ArrayList<>();

    private List<MsgTendBean> msgList=new ArrayList<>();
    //得到装有TendItems满负荷的数据，但我们只需要TendItems部分域属性，
    //对于评论comment, 是commentUserName,和comment，commentPubTime  对应的TendComments bean
    //对于点赞,则是 commentUserName,和时间    对应的TendLikes bean
    public MsgTendAdapter(Context context,List<MsgTendBean> msgList){
        this.context=context;
        this.msgList=msgList;

    }

    @Override
    public MsgTendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MsgTendViewHolder holder = new MsgTendViewHolder(LayoutInflater.from(
                context).inflate(R.layout.recyclerview_msg_item_2, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MsgTendViewHolder holder, int position) {
        if(msgList.size()==0)
            return;
          MsgTendBean msg=  msgList.get(position);
        String str1= msg.getCommentUserName();
        String strC="评论你：";

        holder.ll_msg.setTag(msg.getTend());
        String str2 =  msg.getCommentContent().trim();
        if(str2.length()>16) {
            str2 = str2.substring(0, 15);
//            StringBuffer sb=new StringBuffer(str2);
//            sb=sb.append("...");
            str2=str2.concat("..");
//            str2=sb.toString();
        }
        //一定要在fromHtml写完全部字符串，否则变色失效
        Spanned htmlStr = Html.fromHtml("<font color=#5AADF1>" + str1 + "</font>" + strC + str2);
        holder.tv_tendComment.setText(htmlStr);
        String pubTime = TimeUtils2.getDescriptionTimeFromTimestamp(TimeUtils2.stringToLong( msg.getCommentTime(), TimeUtils2.FORMAT_DATE_TIME_SECOND));
        holder.tv_tendCommentPubtime.setText(pubTime);


        Glide.with(context).load( msg.getCommentHeadUrl()).
                error(R.drawable.ic_default_null_gray)
                .into(holder.roundIv_tendenComment_head);
    }


    @Override
    public int getItemCount() {
//        MyLog.i("itemCOunt_ "+dataList.size());
        if(msgList==null)
            return 0;
        return msgList.size();
//        return  2;
    }

    class MsgTendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv_tendComment,tv_tendCommentPubtime;
        private RoundedImageView roundIv_tendenComment_head;
        private LinearLayout ll_msg;
        public MsgTendViewHolder(View itemView) {
            super(itemView);
            tv_tendComment= (TextView) itemView.findViewById(R.id.tv_tendComment);
            tv_tendCommentPubtime= (TextView) itemView.findViewById(R.id.tv_tendCommentPubtime);
            roundIv_tendenComment_head= (RoundedImageView) itemView.findViewById(R.id.roundIv_tendComment_head);
            ll_msg= (LinearLayout) itemView.findViewById(R.id.ll_msg);
            ll_msg.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            //只有ll_msg的点击事件
            if(itemClickListener!=null){
//                        MyLog.d("topview click not null");
                itemClickListener.onItemClick(view,(TendItems)view.getTag());
            }

        }
    }//MsgViewHolder

    public  interface onRecyclerViewItemClickListener {
        void onItemClick(View v,TendItems tend);
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
//        MyLog.v("设置listener");
        this.itemClickListener = listener;
        //  Log.d("ddd", itemClickListener.toString());
    }
}//MsgTendAdapter
