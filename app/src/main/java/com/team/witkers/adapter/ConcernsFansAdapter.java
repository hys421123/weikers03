package com.team.witkers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.bean.ConcernBean;
import com.team.witkers.bean.ConcernFans;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lenovo on 2016/11/28.
 */

public class ConcernsFansAdapter extends RecyclerView.Adapter< ConcernsFansAdapter.ConcernsFansViewHolder> {

    private Context context;
    private List<ConcernFans> dataList;
    private List<ConcernFans> concernFansList = new ArrayList<>();
    private List<ConcernFans> meConcernsList=null;
//    private  boolean isConcerned3;//判断是否 被 本用户关注的 状态


    public ConcernsFansAdapter(Context context, List<ConcernFans> dataList){
        this.context=context;
        this.dataList=dataList;
    }
    public ConcernsFansAdapter(Context context, List<ConcernFans> dataList,List<ConcernFans> meConcernsList){
        this.context=context;
        this.dataList=dataList;
        this.meConcernsList=meConcernsList;
    }

    @Override
    public ConcernsFansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_concernfans_item, parent,false);
        ConcernsFansViewHolder viewHolder=new ConcernsFansViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ConcernsFansViewHolder holder, final int position) {
        final ConcernFans concernFans=dataList.get(position);
        // 判断是否 被  我 户关注的 状态
        final boolean[] isConcerned3 = {meConcernsList.contains(concernFans)};
        //TODO 设置头像
        if(concernFans.getHeadUrl()!=null){

            Glide.with(context)
                    .load(concernFans.getHeadUrl())
                    .into(holder.concerns2_head);
        }else{
            Glide.with(context)
                    .load(R.drawable.ic_default_null_gray)
                    .into(holder.concerns2_head);
        }

        // TODO: 2016/11/28   设置名字 简介
        holder.tv_concerns2_name.setText(concernFans.getUserName());
        holder.tv_concerns2_info.setText(concernFans.getInfo());

        // TODO: 2016/11/29  设置 关注图片
        //若关注或粉丝是自己的话，则不出现
         if( concernFans.getUserName().equals(MyApplication.mUser.getUsername()) ){
            holder.iv_isConcerned.setVisibility(View.INVISIBLE);
             return;
        }

        if(meConcernsList!=null&&meConcernsList.size()!=0)
        { // meConcernsList not null 表示 本用户 有关注的人，  这种情况才加关注
            // 如果 我的关注者 中 含有 他人的粉丝或 关注时，表明 与我互粉的 关联
//            MyLog.v("meConcernsList not null");
            for(int i=0;i<meConcernsList.size();i++){
              MyLog.v("meConcernsList_userName_ "+ meConcernsList.get(i).getUserName());
            }
            MyLog.d("concernFans_userName_"+ concernFans.getUserName() );
            MyLog.e(meConcernsList.contains(concernFans)+"");

            if(isConcerned3[0]){
                MyLog.e(" is Concerned true");
                Glide.with(context)
                        .load(R.drawable.concerned2)
                        .into(holder.iv_isConcerned);

            }else{
                Glide.with(context)
                        .load(R.drawable.unconcerned2)
                        .into(holder.iv_isConcerned);
            }
        }// meConcernsList not null

        // TODO: 2016/11/30  添加 关注的 点击事件

        holder.iv_isConcerned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLog.d("click concerned");
                if(isConcerned3[0]){//若已经互相关注了，  则取消关注
                    Glide.with(context)
                            .load(R.drawable.unconcerned2)
                            .into(holder.iv_isConcerned);

                    MyLog.e(" 按键 取消 关注");
                    MyLog.v("position_ "+position);

                    if(meConcernsList!=null&&meConcernsList.size()!=0){
                        meConcernsList.remove(concernFans);
                    }
                    unConcernPerson3(meConcernsList,concernFans.getUserName());


                    isConcerned3[0] =false;
                }else{// 关注这个对象
                    Glide.with(context)
                            .load(R.drawable.concerned2)
                            .into(holder.iv_isConcerned);

                    MyLog.e(" 按键 关注 对象");
                    MyLog.v("position_ "+position);

                    if(meConcernsList!=null){
                        meConcernsList.add(concernFans);
                    }else{
                        meConcernsList=new ArrayList<ConcernFans>();
                        meConcernsList.add(concernFans);
                    }
                    concernPerson3(meConcernsList,concernFans.getUserName());

                    isConcerned3[0] =true;
                }//else

            }//onClick
        });


    }//onBindViewHolder

    //我 点击 关注 他人
    private  void concernPerson3(final List<ConcernFans> meConcernsList,String otherUserName){
        BmobQuery<ConcernBean> query = new BmobQuery<ConcernBean>();
        // 修改我自己的关注者信息
        query.addWhereEqualTo("name",MyApplication.mUser.getUsername());
        query.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    MyLog.v("concernPerson__");
                 ConcernBean   concernBean = list.get(0);
                    concernBean.setConcernsList(meConcernsList);


                    concernBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("我关注别人1成功");
                            }else{
                                MyLog.i("我关注别人1失败："+e.getMessage());
                            }
                        }
                    });

                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });

        // 我 成为别人 的粉丝
        BmobQuery<ConcernBean> query2 = new BmobQuery<ConcernBean>();
        query2.addWhereEqualTo("name",otherUserName);
        query2.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                ConcernBean   fansBean = list.get(0);
                    List<ConcernFans> fansList;
                    MyUser myself=MyApplication.mUser;
                    ConcernFans fans=new ConcernFans(myself.getHeadUrl(),myself.getUsername(),myself.getIntroduce());

                    if(fansBean.getFansList()==null){
                        fansList=new ArrayList<ConcernFans>();
                        fansList.add(fans);
                        fansBean.setFansList(fansList);
                    }else {

                        fansBean.getFansList().add(fans);
                    }

                    fansBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("我成为粉丝1成功");
                            }else{
                                MyLog.i("我成为粉丝1失败："+e.getMessage());
                            }
                        }//done
                    });
                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });
    }

    private void unConcernPerson3(final List<ConcernFans> meConcernsList,String otherUserName) {
        BmobQuery<ConcernBean> query = new BmobQuery<ConcernBean>();
        // 我 取消 关注 别人的信息
        query.addWhereEqualTo("name",MyApplication.mUser.getUsername());
        query.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    MyLog.v("unconcernPerson__");
                 ConcernBean   concernBean = list.get(0);
                   concernBean.setConcernsList(meConcernsList);


                    concernBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("我取消关注别人 1成功");
                            }else{
                                MyLog.i("我取消关注别人 1失败："+e.getMessage());
                            }
                        }
                    });

                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });



        // 从 别人的 粉丝中剔除我
        BmobQuery<ConcernBean> query2 = new BmobQuery<ConcernBean>();
        query2.addWhereEqualTo("name",otherUserName);
        query2.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                  ConcernBean  fansBean = list.get(0);
                    List<ConcernFans> fansList;
                    MyUser myself=MyApplication.mUser;
                    ConcernFans fans=new ConcernFans(myself.getHeadUrl(),myself.getUsername(),myself.getIntroduce());

                    if(fansBean.getFansList()==null){
                        MyLog.e("关注列表 为空，无法取消关注！！");
                    }else {
                        fansBean.getFansList().remove(fans);
                    }

                    fansBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("用户 取消 我 1成功");
                            }else{
                                MyLog.i("用户 取消 我 1失败："+e.getMessage());
                            }
                        }//done
                    });
                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });


    }//unConcernPerson3



    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ConcernsFansViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView concerns2_head;
        private TextView tv_concerns2_name,   tv_concerns2_info;
        private ImageView iv_isConcerned;
        LinearLayout itemView;

        public ConcernsFansViewHolder(View itemView) {
            super(itemView);
            concerns2_head= (RoundedImageView) itemView.findViewById(R.id.concerns2_head);
            tv_concerns2_name= (TextView) itemView.findViewById(R.id.tv_concerns2_name);
            tv_concerns2_info= (TextView) itemView.findViewById(R.id.tv_concerns2_info);
            iv_isConcerned= (ImageView) itemView.findViewById(R.id.iv_isConcerned);


        }


    }//ViewHolder
}//ConcernsFansAdapter_cls
