package com.team.witkers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintButton;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.bean.ConcernBean;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.CropAvatarEvent;
import com.team.witkers.utils.RegexUtils;
import com.team.witkers.views.KeyEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * 注册
 * Created by zcf on 2016/4/15.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout clayout;
    private Toolbar registerToolbar;
    private KeyEditText et_username,et_phonenum,et_password;
    private TintButton btn_register;
    private EditText editText;
    private ImageView iv_add_photo;
    private TextView tv_register_terms;
    //上传至服务器的头像网络命名
    private String userHeadUrl;
    private ProgressDialog mDialog;

    //向VerifyActivity传递手机号码
    private static final int REQUEST_FOR_PHONENUM = 0;

    private static final int REQUESTCODE_PIC = 1;//打开相册

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_2);
        EventBus.getDefault().register(this);

        initView();
        initToolBar();
        initClickListener();

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

    private void initView() {

        clayout= (CoordinatorLayout) findViewById(R.id.clayout);
        editText= (EditText) findViewById(R.id.editText);
        et_username= (KeyEditText) findViewById(R.id.et_username);
        et_password= (KeyEditText) findViewById(R.id.et_password);
        et_phonenum= (KeyEditText) findViewById(R.id.et_phonenum);
        btn_register= (TintButton) findViewById(R.id.btn_register);
        iv_add_photo= (ImageView) findViewById(R.id.iv_add_photo);
        registerToolbar= (Toolbar) findViewById(R.id.registerToolbar);
        tv_register_terms= (TextView) findViewById(R.id.tv_register_terms);

        editText.setFocusable(true);
        editText.requestFocus();
    }//initView

    private void initToolBar(){
        registerToolbar.setTitle("");
        setSupportActionBar(registerToolbar);
    }

    private void initClickListener(){
        btn_register.setOnClickListener(this);
        iv_add_photo.setOnClickListener(this);
        tv_register_terms.setOnClickListener(this);

        et_username.addTextChangedListener(textWatcher);
        et_password.addTextChangedListener(textWatcher);
        et_phonenum.addTextChangedListener(textWatcher);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_add_photo:
                Intent picIntent = new Intent(Intent.ACTION_PICK,null);
                picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(picIntent,REQUESTCODE_PIC);
                break;
            case R.id.btn_register:
                //简单版，通过用户名密码注册

//                MyUser myUser =new MyUser();
//                myUser.setUsername(et_username.getText().toString().trim());
//                myUser.setPassword(et_password.getText().toString().trim());
//                if(userHeadUrl!=null) {
//                    MyLog.v("register_headUrl "+userHeadUrl);
//                    myUser.setHeadUrl(userHeadUrl);
//                }else
//                    MyLog.e("userHeadUrl_null");
//
//                mDialog = new ProgressDialog(this, "正在注册");
//                mDialog.show();
//                MyLog.i("register");
//                myUser.signUp(new SaveListener<MyUser>() {
//                    @Override
//                    public void done(MyUser myUser, BmobException e) {
//                        if(e==null){
//
//                            ConcernBean concernBean = new ConcernBean();
//                            concernBean.setName(myUser.getUsername());
//                            concernBean.save(new SaveListener<String>() {
//                                @Override
//                                public void done(String s, BmobException e) {
//                                    if(e==null){
//                                        MyLog.v("注册成功");
//                                        Toast.makeText(RegisterActivity.this,"注册成功:",Toast.LENGTH_SHORT).show();
//                                        mDialog.dismiss();
//                                        finish();
//                                    }else{
//                                        mDialog.dismiss();
//                                        Toast.makeText(RegisterActivity.this,"注册失败,"+"--msg--" + e.getMessage(),Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }else{
//                            mDialog.dismiss();
//                            Toast.makeText(RegisterActivity.this,"注册失败,"+"--msg--" + e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }//else
//                    }
//                });





//                BmobUser bu = new BmobUser();
//                bu.setUsername("sendi");
//                bu.setPassword("123456");
//                bu.setEmail("sendi@163.com");
////注意：不能用save方法进行注册
//                bu.signUp(new SaveListener<MyUser2>() {
//                    @Override
//                    public void done(MyUser2 s, BmobException e) {
//                        if(e==null){
//                            toast("注册成功:" +s.toString());
//                        }else{
//                            loge(e);
//                        }
//                    }
//                });

                //下面是完整版的，关联手机号
                String phoneNum=et_phonenum.getText().toString().trim();
                //判断输入的是否是手机号
                if (!isPhoneNumber(phoneNum)) {
                    Toast.makeText(this,"手机号不正确,请重新输入",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    MyUser myUser2=new MyUser();
                    if(userHeadUrl!=null) {
                        MyLog.v("register_headUrl "+userHeadUrl);
                        myUser2.setHeadUrl(userHeadUrl);
                    }else {
                        MyLog.e("userHeadUrl_null");
                        myUser2.setHeadUrl(MyApplication.default_roundUrl);
                    }

                    myUser2.setUsername(et_username.getText().toString().trim());
                    myUser2.setMobilePhoneNumber(et_phonenum.getText().toString().trim());
                    myUser2.setPassword(et_password.getText().toString().trim());
                    //请求验证码
                    if(userHeadUrl==null||userHeadUrl.equals("")){
                        userHeadUrl="error";
                    }
                    myUser2.setHeadUrl(userHeadUrl);
                   sendSms(myUser2);
                }//if

                break;
            case R.id.tv_register_terms:
                Toast.makeText(this,"terms",Toast.LENGTH_SHORT).show();
                break;

        }
    }//onClick
    /**
     * 发送短信并且验证
     */

    private void sendSms(final MyUser myUser) {
        final String phoneNum2= myUser.getMobilePhoneNumber();
//        Intent intent=new Intent(RegisterActivity.this,RegisterVerifyActivity.class);
//        intent.putExtra("phoneNum",phoneNum2);
//        intent.putExtra("myUser",myUser2);
//        MyLog.v("send-myUser2-phoneNum-"+myUser2.getMobilePhoneNumber());
//        startActivity(intent);
//        finish();

        MyLog.i("sendSms");
       BmobSMS.requestSMSCode(phoneNum2, "微客团队", new QueryListener<Integer>() {
           @Override
           public void done(Integer integer, BmobException ex) {
               if (ex == null) {//验证码发送成功
                   MyLog.v("验证码发送成功 ");
//                    MyLog.v("bmob 短信id：" + smsId);//用于查询本次短信发送详情
                   Intent intent=new Intent(RegisterActivity.this,RegisterVerifyActivity.class);
                   intent.putExtra("phoneNum",phoneNum2);
                   intent.putExtra("myUser2", myUser);
//                   intent.putExtra("userHeadUrl",userHeadUrl);
//                    MyLog.v("myUser2-phoneNum-"+myUser2.getMobilePhoneNumber());
                   startActivity(intent);
                   finish();
               }
               else {
                   MyLog.v("bmob 短信id：" + integer);//用于查询本次短信发送详情
                   MyLog.v("ex--" + ex.getMessage());
                   Toast.makeText(RegisterActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
               }//else
           }//done
       });

    }

    private boolean isPhoneNumber(String phoneNumber) {
        String expression = RegexUtils.PHONE_NUMBER_FULL;
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }
    /**
     * 更新数据
     *
     * @param event 事件
     */
    @Subscribe
    public void onEventMainThread(CropAvatarEvent event) {


        File imageFile=event.getFile();
        userHeadUrl=event.getbFileName();
//        MyLog.v("eventBus_headUrl_"+userHeadUrl);

        Picasso.with(this)
                .load(imageFile)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(iv_add_photo);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_PIC:

                    if (data == null || data.getData() == null){
                        return;
                    }
                    Intent intent = new Intent(this, CropActivity.class);
                    //直接在Extra里面添加 uri，传递到CropActivity中
                    intent.putExtra("uri",data.getData());
                    startActivity(intent);
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            enableRegisterBtn();
        }
    };
    private void enableRegisterBtn() {
        //完整版  也要填手机号
//        btn_register.setEnabled(et_username.getText().length() != 0 && et_password.getText().length() != 0
//                &&et_phonenum.getText().length()!=0);

        //简单版   不用填手机号
        btn_register.setEnabled(et_username.getText().length() != 0 && et_password.getText().length() != 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("RegisterAct destroy");
        EventBus.getDefault().unregister(this);
    }
}
