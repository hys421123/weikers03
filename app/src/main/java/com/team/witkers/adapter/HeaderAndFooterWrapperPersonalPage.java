package com.team.witkers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.concernFans.ConcernsActivity;
import com.team.witkers.bean.ConcernBean;
import com.team.witkers.bean.ConcernFans;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by hys on 2016/8/23.
 */
public class HeaderAndFooterWrapperPersonalPage<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

// 从 PersonalHomePageActivity2 传过来的参数
    private static final int BASE_ITEM_TYPE_HEADER = 100;
    private static final int BASE_ITEM_TYPE_FOOTER = 200;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mInnerAdapter;
    private MyUser person;
    private Context context;
    private int pubMissionNum,concernNum,fansNum;
    //判断我是否在粉丝中 ，状态是否为 关注中
    private Boolean meConcerned;
    private ConcernBean concernBean;
    private ConcernBean fansBean;


    public HeaderAndFooterWrapperPersonalPage(Context context, MyUser person, RecyclerView.Adapter adapter){
        this.context=context;
        this.person=person;
        this.mInnerAdapter=adapter;
    }
    public HeaderAndFooterWrapperPersonalPage(Context context, MyUser person, RecyclerView.Adapter adapter,
                                              int pubMissionNum,int concernNum,int fansNum,Boolean isConcerned){
        this.context=context;
        this.person=person;
        this.mInnerAdapter=adapter;
        this.pubMissionNum = pubMissionNum;
        this.concernNum = concernNum;
        this.fansNum = fansNum;
        this.meConcerned = isConcerned;
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
            MyViewHolder holder = new MyViewHolder(mFootViews.get(viewType),viewType);
            return holder;
        }
//        MyLog.i("else_viewType_ "+viewType);
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position))
        {
            Glide.with(context).load(person.getHeadUrl()).error(R.drawable.ic_default_null_gray)
                    .into(((MyViewHolder)holder).roundIv_personpage_head);
//            MyLog.i(pubMissionNum+"---"+concernNum+"----"+fansNum+"---"+isConcerned);
            if(meConcerned){//关注中
                ((MyViewHolder)holder).rl_concern.setBackgroundResource(R.drawable.img_cancel_concern);
            }else{//加关注
                ((MyViewHolder)holder).rl_concern.setBackgroundResource(R.drawable.img_add_concern);
            }
            ((MyViewHolder)holder).tv_fans.setText(fansNum+"");
            ((MyViewHolder)holder).tv_focus.setText(concernNum+"");
            ((MyViewHolder)holder).tv_tendencies.setText(pubMissionNum+"");
            ((MyViewHolder) holder).tv_introduce.setText(person.getIntroduce());
            ((MyViewHolder)holder).rl_concern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyUser myself = BmobUser.getCurrentUser(MyUser.class);
                    if(myself==null){
                        MyToast.showToast(context,"亲，你还没有登陆哦！");
                        return;
                    }
                    if(person.getUsername().equals(myself.getUsername())){
                        MyToast.showToast(context,"亲，不能关注自己啊");
                        return;
                    }
                    MyLog.i("myself-->"+myself.getUsername());////////////////
                    if(meConcerned){//点击  关注中    将图片换为  加关注(取消关注状态)
                        ((MyViewHolder)holder).rl_concern.setBackgroundResource(R.drawable.img_add_concern);
                        unConcernPerson3(person,myself);
                        meConcerned = false;
                        MyToast.showToast(context,"onClick  "+meConcerned);
                    }else{//点击  加关注(取消关注状态)    将图片换为  关注中
                        MyLog.i(" 加关注啊11");
                        ((MyViewHolder)holder).rl_concern.setBackgroundResource(R.drawable.img_cancel_concern);
                        concernPerson3(person,myself);
                        meConcerned = true;
                        MyToast.showToast(context,"onClick  "+meConcerned);
                    }
                }
            });
            return;
        } if (isFooterViewPos(position))
        {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }



    @Override
    public int getItemCount() {
        return getHeadersCount()+mInnerAdapter.getItemCount()+getFootersCount();
    }

    //点击 关注 他人
    private  void concernPerson3(final MyUser person, final MyUser myself){
        BmobQuery<ConcernBean> query = new BmobQuery<ConcernBean>();
        // 修改myself的关注者信息
        query.addWhereEqualTo("name",myself.getUsername());
        query.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    MyLog.v("concernPerson__");
                    concernBean = list.get(0);
                    List<ConcernFans> concernsList;
                    ConcernFans concern=new ConcernFans(person.getHeadUrl(),person.getUsername(),person.getIntroduce());

                    if(concernBean.getConcernsList()==null){
                        concernsList=new ArrayList<ConcernFans>();
                        concernsList.add(concern);
                        concernBean.setConcernsList(concernsList);
                    }else {
                         concernBean.getConcernsList().add(concern);
                    }

                    // 如果粉丝中 含有关注的人， 及可加互粉
//                    if(concernBean.getFansList()!=null&&concernBean.getFansList().contains(concern)){
//                        int position=concernBean.getFansList().indexOf(concern);
////                        concernBean.getFansList().get(position).setConcerned(true);
//                    }

//                    // 修改myself 的 粉丝信息fansList， 主要是对 isConcerned(是否互相关注) 属性进行修改
//                    // 新建一个交集
//                    List<ConcernFans> intersectionFansList1=new ArrayList<ConcernFans>();
//                    intersectionFansList1.addAll(concernBean.getFansList()) ;
//                    if(intersectionFansList1!=null) {
////                        取交集
//                        intersectionFansList1.retainAll(concernBean.getConcernsList());
//                        if (intersectionFansList1.size() != 0) {// 粉丝、关注 两者有交集
//                            concernBean.getFansList().removeAll(intersectionFansList1);//取 差集
//                            // 修改 交集的 isConcerned 属性
//                            for (ConcernFans fans : intersectionFansList1) {
//                                fans.setConcerned(true);
//                            }
//                            //修改属性后，  并集  ,此时 完成 对getConcernsList的修改
//                            concernBean.getFansList().addAll(intersectionFansList1);
//                        }//有交集
//                    }//fansList2 not null




                    concernBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("用户关注1成功");
                            }else{
                                MyLog.i("用户关注1失败："+e.getMessage());
                            }
                        }
                    });

                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });

        // 修改被关注者(其他人)的 粉丝信息
        BmobQuery<ConcernBean> query2 = new BmobQuery<ConcernBean>();
        query2.addWhereEqualTo("name",person.getUsername());
        query2.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    fansBean = list.get(0);
                    List<ConcernFans> fansList;
                    ConcernFans fans=new ConcernFans(myself.getHeadUrl(),myself.getUsername(),myself.getIntroduce());

                    if(fansBean.getFansList()==null){
                        fansList=new ArrayList<ConcernFans>();
//                        if(fansBean.getConcernsList()!=null&&fansBean.getConcernsList().contains(fans)){
//                            MyLog.v("person set_concern_true_0");
////                            fans.setConcerned(true);
//                        }
                        fansList.add(fans);
                        fansBean.setFansList(fansList);
                    }else {
//                        if(fansBean.getConcernsList()!=null&&fansBean.getConcernsList().contains(fans)){
////                            int position=fansBean.getConcernsList().indexOf(fans);
//                            MyLog.v("person set_concern_true_1");
////                            fans.setConcerned(true);
//                        }
                        fansBean.getFansList().add(fans);
                    }




//                    // 修改person中 的 粉丝信息fansList， 主要是对 isConcerned(是否互相关注) 属性进行修改
//                    //复制 getConcernsList() 值，而不破坏其中的值
//                    List<ConcernFans> intersectionFansList2=new ArrayList<ConcernFans>();
//                    //得到 关注列表
//                    intersectionFansList2.addAll(fansBean.getFansList()) ;
//
//
//                    if(fansBean.getConcernsList()!=null){
//                        //取 交集
//                    intersectionFansList2.retainAll(concernBean.getConcernsList());
//                        if (intersectionFansList2.size() != 0) {// 粉丝、关注 两者有交集
//                            fansBean.getFansList().removeAll(intersectionFansList2);//取 差集
//
//                            // 修改 交集的 isConcerned 属性
//                            for (ConcernFans fans2:intersectionFansList2) {
//                                fans2.setConcerned(true);
//                            }
//
//                            //修改属性后，  并集  ,此时 完成 对getConcernsList的修改
//                            fansBean.getFansList().addAll(intersectionFansList2);
//                        }//有交集
//
//                    }//getConcernsList not null



                    fansBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("用户成为粉丝1成功");
                            }else{
                                MyLog.i("用户成为粉丝1失败："+e.getMessage());
                            }
                        }//done
                    });
                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });

    }

    private void unConcernPerson3(MyUser other, final MyUser myself) {
        BmobQuery<ConcernBean> query = new BmobQuery<ConcernBean>();
        // 取消 myself的关注者信息
        query.addWhereEqualTo("name",myself.getUsername());
        query.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    MyLog.v("unconcernPerson__");
                    concernBean = list.get(0);
                    List<ConcernFans> concernsList;
                    ConcernFans concern=new ConcernFans(person.getHeadUrl(),person.getUsername(),person.getIntroduce());

                    if(concernBean.getConcernsList()==null){
                        MyLog.e("关注列表 为空，无法取消关注！！");
                    }else {
                        concernBean.getConcernsList().remove(concern);
                    }

                    //若 含有关注的人被 取消，则去掉互粉
//                    if(concernBean.getFansList()!=null&&concernBean.getFansList().contains(concern)){
//                         int position=   concernBean.getFansList().indexOf(concern);
////                        concernBean.getFansList().get(position).setConcerned(false);
//                    }

                    concernBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("用户取消关注1成功");
                            }else{
                                MyLog.i("用户取消关注1失败："+e.getMessage());
                            }
                        }
                    });

                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });



        // 取消 被关注者(其他人)的 粉丝信息
        BmobQuery<ConcernBean> query2 = new BmobQuery<ConcernBean>();
        query2.addWhereEqualTo("name",person.getUsername());
        query2.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    fansBean = list.get(0);
                    List<ConcernFans> fansList;
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
                                MyLog.i("用户 取消成为粉丝1成功");
                            }else{
                                MyLog.i("用户 取消成为粉丝1失败："+e.getMessage());
                            }
                        }//done
                    });
                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });


    }//unConcernPerson3

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RoundedImageView roundIv_personpage_head;
        TextView tv_tendencies,tv_focus,tv_fans,tv_introduce;
        RelativeLayout rl_concern;
        LinearLayout ll_rv_pubMissions,ll_rv_concerns,ll_rv_fans;
        public MyViewHolder(View itemView,int viewType) {
            super(itemView);
            if(viewType==BASE_ITEM_TYPE_HEADER){
                roundIv_personpage_head= (RoundedImageView) itemView.findViewById(R.id.roundIv_personpage_head);
                rl_concern = (RelativeLayout) itemView.findViewById(R.id.rl_concern);
                tv_fans = (TextView) itemView.findViewById(R.id.tv_fans);
                tv_focus  = (TextView) itemView.findViewById(R.id.tv_focus);
                tv_tendencies = (TextView) itemView.findViewById(R.id.tv_tendencies);
                tv_introduce = (TextView) itemView.findViewById(R.id.tv_introduce);

                ll_rv_pubMissions= (LinearLayout) itemView.findViewById(R.id.ll_rv_pubMissions);
                ll_rv_concerns= (LinearLayout) itemView.findViewById(R.id.ll_rv_concerns);
                ll_rv_fans= (LinearLayout) itemView.findViewById(R.id.ll_rv_fans);

                ll_rv_pubMissions.setOnClickListener(this);
                ll_rv_fans.setOnClickListener(this);
                ll_rv_concerns.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ll_rv_pubMissions:
                    MyLog.i("ll_rv_pubMissions");
                    break;

                case R.id.ll_rv_concerns:
//                    MyLog.i("ll_rv_other_concerns");

                    Intent intentc=new Intent(context,ConcernsActivity.class);
                    intentc.putExtra("userName1",person.getUsername());
                    intentc.putExtra("title1","他的关注");
                    // 处理别人的关注
                    intentc.putExtra("isOthers",true);
                    context.startActivity(intentc);

                    break;

                case R.id.ll_rv_fans:
                    MyLog.i("ll_rv_fans");
                    Intent intentf=new Intent(context,ConcernsActivity.class);
                    intentf.putExtra("userName1",person.getUsername());
                    intentf.putExtra("title1","他的粉丝");
                    intentf.putExtra("isFans",true);
                    // 处理别人的粉丝
                    intentf.putExtra("isOthers",true);
                    context.startActivity(intentf);
                    break;
            }//switch
        }
    }//MyViewHolder_cls
}
