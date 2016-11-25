package com.team.witkers.adapter;

import android.content.Context;
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
    private Boolean isConcerned;
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
        this.isConcerned = isConcerned;
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
            if(isConcerned){//关注中
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
                    if(isConcerned){//点击  关注中    将图片换为  加关注(取消关注状态)
                        ((MyViewHolder)holder).rl_concern.setBackgroundResource(R.drawable.img_add_concern);
                        unConcernPerson(person,myself);
                        isConcerned = false;
                        MyToast.showToast(context,"onClick  "+isConcerned);
                    }else{//点击  加关注(取消关注状态)    将图片换为  关注中
                        MyLog.i(" 加关注啊11");
                        ((MyViewHolder)holder).rl_concern.setBackgroundResource(R.drawable.img_cancel_concern);
                        concernPerson3(person,myself);
                        isConcerned = true;
                        MyToast.showToast(context,"onClick  "+isConcerned);
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

//    private void concernPerson2(final MyUser other,final MyUser myself){
//        BmobRelation relation=new BmobRelation();
//        MyUser other2=new MyUser();
//        other2.setObjectId(MyApplication.objId);
//
//        MyLog.i("other2");
//        relation.add(other2);
//        MyApplication.mUser.setConcernPerson(relation);
//        MyApplication.mUser.update(new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null){
//                    MyLog.v("关注更新成功");
//
//                }else{
//                    //主要更新失败原因，只有登录用户才能修改与自己相关的信息
//                    MyLog.e("关注更新失败"+e.getMessage());
//                }
//            }//done
//        });
//
//    }//concernPerson2

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
                    ConcernFans concern=new ConcernFans(person.getHeadUrl(),person.getUsername(),person.getIntroduce(),true);

                    if(concernBean.getConcernsList()==null){
                        concernsList=new ArrayList<ConcernFans>();
                        concernsList.add(concern);
                        concernBean.setConcernsList(concernsList);
                    }else {
                         concernBean.getConcernsList().add(concern);
                    }

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
                    ConcernFans fans=new ConcernFans(myself.getHeadUrl(),myself.getUsername(),myself.getIntroduce(),true);

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


    private void concernPerson(final MyUser person, final MyUser myself) {

//        MyLog.v("concernPerson");
        BmobQuery<ConcernBean> query = new BmobQuery<ConcernBean>();
        query.addWhereEqualTo("name",myself.getUsername());
        query.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                    MyLog.v("concernPerson__");
                     concernBean = list.get(0);
                    BmobRelation relation=new BmobRelation();
                    relation.add(person);

                    concernBean.setConcerns(relation);
                    concernBean.increment("concernsNum");
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

        BmobQuery<ConcernBean> query2 = new BmobQuery<ConcernBean>();
        query2.addWhereEqualTo("name",person.getUsername());
        query2.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e == null){
                     fansBean = list.get(0);
                    BmobRelation relation=new BmobRelation();
                    relation.add(myself);

                    fansBean.increment("fansNum");
                    fansBean.setFans(relation);
                    fansBean.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyLog.i("用户被关注1成功");
                            }else{
                                MyLog.i("用户被关注1失败："+e.getMessage());
                            }
                        }
                    });
                }else{
                    MyLog.i("e-->"+e);
                }
            }
        });




    }//concernPerson

    private void unConcernPerson2(MyUser other, MyUser myself) {

        BmobRelation relation=new BmobRelation();
        relation.remove(other);
        myself.setConcernPerson(relation);
        myself.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyLog.v("取消_关注更新成功");

                }else{
                    MyLog.e("取消_关注更新失败"+e.getMessage());
                }
            }//done
        });

    }

    private void unConcernPerson(MyUser person, MyUser myself) {
        BmobRelation relation = new BmobRelation();
        BmobRelation relation2 = new BmobRelation();
        relation.remove(person);
        relation2.remove(myself);
        myself.setObjectId(myself.getObjectId());
        person.setObjectId(person.getObjectId());
        myself.setConcernPerson(relation);//我取消关注别人
        person.setFansPerson(relation2);//别人减少我这个fans
        myself.increment("concernsNum",-1);
        myself.update(myself.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyLog.i("用户取消关注成功");
                }else{
                    MyLog.i("用户取消关注失败："+e.getMessage());
                }
            }
        });

        person.increment("fansNum",-1);
        person.update(person.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyLog.i("用户减少fans成功");
                }else{
                    MyLog.i("用户减少fans失败："+e.getMessage());
                }
            }
        });
    }

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
                    MyLog.i("ll_rv_concerns");
                    break;

                case R.id.ll_rv_fans:
                    MyLog.i("ll_rv_fans");
                    break;
            }//switch
        }
    }//MyViewHolder_cls
}
