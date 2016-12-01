package com.team.witkers.fragment;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.LoginActivity;
import com.team.witkers.activity.concernFans.ConcernsActivity;
import com.team.witkers.activity.editInfo.EditPersonalInfoActivity;
import com.team.witkers.activity.pubclaim.MyClaimActivity;
import com.team.witkers.activity.pubclaim.MyPubActivity;
import com.team.witkers.activity.setting.SettingActivity;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.ConcernBean;
import com.team.witkers.bean.ConcernFans;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.liteasysuits.Log;
import com.team.witkers.views.MyPubItem;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by hys on 2016/8/2.
 */
public class MeFragmentLogin extends BaseFragment implements View.OnClickListener{


    private RoundedImageView roundIv_head;
    private MyPubItem customView_myPub,customView_myClaim,customView_setting,customView_logout;
    private MyUser myUser;
    private RelativeLayout rel_editDetail;
    private TextView tv_introduce,tv_tendencies,tv_focus,tv_fans;
    private LinearLayout ll_pubMissions,ll_concerns,ll_fans;

    private int pubMissionNum,concernNum,fansNum;
    private Handler myHandler = new Handler();

    private static final int REQUESTCODE_MELOG2 = 1;//从Mefragment中退出登录，若返回，则至主界面
    @Override
    protected int setContentId() {
        return R.layout.fragment_me_login_2;
    }


    @Override
    protected void initView(View view) {

        roundIv_head= (RoundedImageView) view.findViewById(R.id.roundIv_mefrm_head);
        customView_myPub= (MyPubItem) view.findViewById(R.id.customView_myPub);
        customView_myClaim= (MyPubItem) view.findViewById(R.id.customView_myClaim);
        customView_setting= (MyPubItem) view.findViewById(R.id.customView_setting);
        customView_logout= (MyPubItem) view.findViewById(R.id.customView_logout);
        rel_editDetail = (RelativeLayout) view.findViewById(R.id.rel_editDetail);
        tv_introduce = (TextView) view.findViewById(R.id.tv_introduce);
        tv_tendencies = (TextView) view.findViewById(R.id.tv_tendencies);
        tv_focus = (TextView) view.findViewById(R.id.tv_focus);
        tv_fans = (TextView) view.findViewById(R.id.tv_fans);

        ll_pubMissions= (LinearLayout) view.findViewById(R.id.ll_pubMissions);
        ll_concerns=(LinearLayout) view.findViewById(R.id.ll_concerns);
        ll_fans= (LinearLayout) view.findViewById(R.id.ll_fans);
    }

    private void setUserHead(){
        if(myUser!=null&&myUser.getHeadUrl()!=null){
            String headUrl=myUser.getHeadUrl();
            Glide.with(getActivity())
                    .load(headUrl)
                    .into(roundIv_head);
        }else{
            Glide.with(getActivity())
                    .load(R.drawable.default_head)
                    .into(roundIv_head);
        }
    }

    @Override
    protected void  setListener() {
        customView_myPub.setOnClickListener(this);
        customView_myClaim.setOnClickListener(this);
        customView_setting.setOnClickListener(this);
        customView_logout.setOnClickListener(this);
        rel_editDetail.setOnClickListener(this);

        ll_pubMissions.setOnClickListener(this);
        ll_concerns.setOnClickListener(this);
        ll_fans.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
//        Log.v("MeFrm click");
        switch (view.getId()){
            case R.id.customView_myPub://我的 发布
                MyLog.i("我的发布");
                Intent intentp=new Intent(getActivity(), MyPubActivity.class);
                startActivity(intentp);
                break;

            case R.id.customView_myClaim://我的认领
                MyLog.i("我的认领");
                Intent intentm=new Intent(getActivity(), MyClaimActivity.class);
                startActivity(intentm);
                break;

            case R.id.customView_setting:
                MyLog.i("setting");
                Intent intent=new Intent(getActivity(),SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.customView_logout:
                MyLog.i("logout");
                BmobUser.logOut(); //清除缓存用户对象,退出登录
                Intent intent2=new Intent(getActivity(),LoginActivity.class);
                getActivity().startActivityForResult(intent2,REQUESTCODE_MELOG2);
//                startActivity(intent2);

//                MyToast.showToast(getActivity(),"退出登录");
                break;
            case R.id.rel_editDetail:
                Intent intent3 = new Intent(getActivity(), EditPersonalInfoActivity.class);
                startActivity(intent3);
                MyLog.i("edit detail");
                break;

            case R.id.ll_pubMissions:
                MyLog.i("ll_pubMissions");

                break;

            //点击进入我的 关注
            case R.id.ll_concerns:
                MyLog.i("ll_Me_concerns");
                Intent intentc=new Intent(getActivity(),ConcernsActivity.class);
                intentc.putExtra("userName1",MyApplication.mUser.getUsername());
                intentc.putExtra("title1","我的关注");
                getActivity().startActivity(intentc);
                break;

            // 我的粉丝
            case R.id.ll_fans:
                MyLog.i("ll_fans");
                Intent intentf=new Intent(getActivity(),ConcernsActivity.class);
                intentf.putExtra("userName1",MyApplication.mUser.getUsername());
                intentf.putExtra("title1","我的粉丝");
                intentf.putExtra("isFans",true);
                getActivity().startActivity(intentf);
                break;
        }//switch
    }//onClick

    @Override
    protected void loadDataonResume() {
        //考虑到fragment 的重新显示，而onCreate只能发生一次故把界面填充写在onResume中
//        MyLog.i("meFrm onResume");
        MyUser user=BmobUser.getCurrentUser(MyUser.class);
        if(user==null)
            return;
        else {
            myUser = user;
            getPubMissionNum();//设置三个数量
            if ( myUser.getHeadUrl() != null) {
                setUserHead();
            }
            String introduceStr = myUser.getIntroduce();
            if(introduceStr!=null&&!introduceStr.equals("")){
                tv_introduce.setText(introduceStr);
            }else {
                tv_introduce.setText("还没有简介哦~");
            }

        }//else
    }//loadDataonResume

    private void getPubMissionNum(){
        //查询这个用户发布过的所有任务
        BmobQuery<Mission> query = new BmobQuery<Mission>();
        query.addWhereEqualTo("pubUserName",myUser.getUsername());
        query.findObjects(new FindListener<Mission>() {
            @Override
            public void done(List<Mission> list, BmobException e) {
                if(e!=null){
                    MyToast.showToast(getActivity(),"查询失败"+e);
                }else {
//                    MyLog.i("查询任务数成功" + list.size());
                    pubMissionNum = list.size();
                    getConcernNumAndFansNum();
                }
            }//done
        });
    }

    private  void getConcernNumAndFansNum(){
        BmobQuery<ConcernBean> query=new BmobQuery<>();
        query.addWhereEqualTo("name",myUser.getUsername());

        query.findObjects(new FindListener<ConcernBean>() {
            @Override
            public void done(List<ConcernBean> list, BmobException e) {
                if(e==null){
                    MyLog.v("查询关注成功");

                    ConcernBean concernBean=list.get(0);

                    if(concernBean.getFansList()==null)
                        fansNum=0;
                    else {
                        List<ConcernFans> fansList=concernBean.getFansList();
                        fansNum = fansList.size();
                    }

                    if(concernBean.getConcernsList()==null)
                        concernNum=0;
                    else
                        concernNum=concernBean.getConcernsList().size();
                    MyLog.v("粉丝数ff和关注数_ 自己是否已经关注"+fansNum+"/// "+concernNum+"/// ");

                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_tendencies.setText(pubMissionNum+"");
                            tv_focus.setText(concernNum+"");
                            tv_fans.setText(fansNum+"");
                        }
                    });

                }else{
                    MyLog.e("查询关注失败"+e.getMessage());
                }

            }//done
        });
    }//getConcernAndFans

}//MeFrm_cls
