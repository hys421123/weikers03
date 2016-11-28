package com.team.witkers.activity;


import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintButton;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.Countdown;
import com.team.witkers.views.KeyEditText;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by hys on 2016/7/20.
 */
public class RegisterVerifyActivity extends BaseActivity implements View.OnClickListener{
    private Toolbar registerVerifyToolbar;
    private TextView tv_verify_topstr,tv_verify_resend;
    private KeyEditText et_verify_num;
    private TintButton btn_verify;
    private ProgressDialog mDialog;

    private String phoneNum="00";
    private MyUser myUser;
    private  String userHeadUrl;

    @Override
    protected int setContentId() {
        return R.layout.activity_register_verify_2;
    }


    @Override
    protected void initView() {
        registerVerifyToolbar= (Toolbar) findViewById(R.id.registerVerifyToolbar);
        tv_verify_topstr= (TextView) findViewById(R.id.tv_verify_topstr);
        tv_verify_resend= (TextView) findViewById(R.id.tv_verify_resend);
        et_verify_num= (KeyEditText) findViewById(R.id.et_verify_num);
        btn_verify= (TintButton) findViewById(R.id.btn_verify);
    }

    @Override
    protected void initToolBar() {
        registerVerifyToolbar.setTitle("");
        setSupportActionBar(registerVerifyToolbar);
    }

    @Override
    protected void setListener() {
        et_verify_num.addTextChangedListener(textWatcher);
        btn_verify.setOnClickListener(this);
        tv_verify_resend.setOnClickListener(this);
    }

    @Override
    protected void getIntentData() {
//        MyLog.d("initData");
        phoneNum=getIntent().getStringExtra("phoneNum");
        myUser = (MyUser) getIntent().getSerializableExtra("myUser2");
//        userHeadUrl=getIntent().getStringExtra("userHeadUrl");
//        MyLog.v("getIntentData--myUser2-phoneNum-"+myUser2.getMobilePhoneNumber());
    }



    @Override
    public void onClick(View view) {
        String verifyCode=et_verify_num.getText().toString().trim();
        switch (view.getId()){
            case R.id.btn_verify:
//                register();//注册
                verifySms(phoneNum,verifyCode);
                break;
            case R.id.tv_verify_resend:
                MyLog.v("resend");
                resendSms();
                break;
        }//switch
    }//onClick

    private void resendSms(){
        Countdown countdown2 = new Countdown(tv_verify_resend, "重新发送(%s)", 60);
        countdown2.start();

//    BmobSMS.requestSMSCode(this, phoneNum, "微客团队", new RequestSMSCodeListener() {
//            @Override
//            public void done(Integer smsId, BmobException ex) {
//                // TODO Auto-generated method stub
////                MyLog.v("done");
//                if (ex == null) {//验证码发送成功
//                    Toast.makeText(RegisterVerifyActivity.this,"验证码再次发送成功",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(RegisterVerifyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
//                }//else
//            }//done
//        });//requestSMSCode

    }//resendSms
    /**
     * 验证 验证码是否正确
     *
     * @param phone      电话号码
     * @param verifyCode 验证码
     */
    private void verifySms(String phone, String verifyCode) {
//        BmobSMS.verifySmsCode(this, phone, verifyCode, new VerifySMSCodeListener() {
//            @Override
//            public void done(BmobException ex) {
//                // TODO Auto-generated method stub
//                if (ex == null) {//短信验证码已验证成功
//                    MyLog.i("bmob--验证通过");
//                    //验证码  正确性
//                    register();//注册
//                } else {
//                    Toast.makeText(RegisterVerifyActivity.this,"验证失败",Toast.LENGTH_SHORT).show();
//                    MyLog.i("bmob--验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
//                }
//            }
//        });

        BmobSMS.verifySmsCode(phone, verifyCode, new UpdateListener() {
            @Override
            public void done(BmobException ex){
                if (ex == null) {//短信验证码已验证成功
                    MyLog.i("bmob--验证通过");
                    //验证码  正确性
                    register();//注册
                } else {
                    Toast.makeText(RegisterVerifyActivity.this,"验证失败",Toast.LENGTH_SHORT).show();
                    MyLog.i("bmob--验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                }//else
            }//done
        });//verifySmsCode
    }

    /**
     * 验证成功，正式注册
     */
    private void register() {
        mDialog = new ProgressDialog(this, "正在注册");
        mDialog.show();
//        MyLog.i("register");
//        MyLog.v("myUser2-phoneNum-"+myUser2.getMobilePhoneNumber());
        myUser.setMobilePhoneNumberVerified(true);//验证通过
        myUser.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e==null){
                    MyLog.v("注册成功");
                    Toast.makeText(RegisterVerifyActivity.this,"注册成功:",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    finish();
                }else{
                    mDialog.dismiss();
                    Toast.makeText(RegisterVerifyActivity.this,"注册失败,"+"--msg--" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }//else
            }
        });
    }
    @Override
    protected void showView() {
        Countdown countdown = new Countdown(tv_verify_resend, "重新发送(%s)", 60);
        countdown.start();

        //接收RegisterActivity传递过来的phoneNum
        phoneNum=getIntent().getStringExtra("phoneNum");

        String str1="我们已给您的手机号码 ";
//        String phoneNum="18717146570";
        String str2=" 发送了一条验证短信。";
        //一定要在fromHtml写完全部字符串，否则变色失效
        Spanned htmlStr = Html.fromHtml(str1+"<font color=#f7c041>"+phoneNum+"</font>"+str2);
        tv_verify_topstr.setText(htmlStr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
//            Toast.makeText(this,"back2_click",Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            enableRegisterBtn();
        }
    };
    private void enableRegisterBtn() {
        btn_verify.setEnabled(et_verify_num.getText().length() != 0);
    }

}
