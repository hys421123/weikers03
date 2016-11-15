package com.team.witkers.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.team.witkers.R;
import com.team.witkers.activity.LoginActivity;
import com.team.witkers.activity.RegisterActivity;
import com.team.witkers.bean.MyUser;

import cn.bmob.v3.BmobUser;


public class MeFragmentOld extends com.team.witkers.base.BaseFragment implements View.OnClickListener{
    private TextView tv_Me_userName;
    private Button registerBtn,loginBtn,logoutBtn;
    private RoundedImageView roundIv_userHead;


    @Override
    protected int setContentId() {
        return R.layout.fragment_me_old;
    }

    @Override
    protected void initView(View view) {
        roundIv_userHead= (RoundedImageView) view.findViewById(R.id.roundIv_userHead);
        tv_Me_userName = (TextView) view.findViewById(R.id.tv_Me_userName);
        registerBtn = (Button) view.findViewById(R.id.registerBtn);
        loginBtn=(Button) view.findViewById(R.id.loginBtn);
        logoutBtn=(Button)view.findViewById(R.id.logoutBtn);

//        Picasso.with(getActivity()).load("http://oac7bvp34.bkt.clouddn.com/me3.jpg")
//                .error(R.mipmap.ic_launcher)
//                .into(roundIv_userHead);

    }

    @Override
    protected void setListener() {
        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    protected void showView() {
//        Uri uri=Uri.parse("http://oac7bvp34.bkt.clouddn.com/me3.jpg");
//        roundIv_userHead.setImageURI(uri);
    }


    //register,login,logout click
    @Override
    public void onClick(View view) {
            switch(view.getId()){
                case R.id.registerBtn:
                   // MyLog.i("registerBtn");
                    getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
                    break;
                case R.id.loginBtn:
//                    MyLog.i("loginBtn");
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                case R.id.logoutBtn:
//                    MyLog.i("logoutBtn");
                    BmobUser.logOut(); //清除缓存用户对象
                    tv_Me_userName.setText("请登录");
                    roundIv_userHead.setVisibility(View.GONE);
//                    BmobUser currentUser = BmobUser.getCurrentUser(getActivity()); // 现在的currentUser是null了
                    break;
            }
    }//onClick

    @Override
    public void onResume() {
        super.onResume();
        try {
            MyUser user = BmobUser.getCurrentUser( MyUser.class);
            if (user != null) {
                // 允许用户使用应用,设置头像，昵称
                tv_Me_userName.setText(user.getUsername());
                roundIv_userHead.setVisibility(View.VISIBLE);
                //设置头像
                if(user.getHeadUrl()==null)
                    Picasso.with(getActivity()).load(R.drawable.default_head)
                            .into(roundIv_userHead);
                else
                    Picasso.with(getActivity()).load(user.getHeadUrl())
                        .into(roundIv_userHead);
            } else {
                // ,设置头像，昵称
                tv_Me_userName.setText("请登录");

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


}
