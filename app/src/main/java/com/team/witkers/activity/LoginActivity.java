package com.team.witkers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendItems;
import com.team.witkers.service.MyService;
import com.team.witkers.views.KeyEditText;
import com.team.witkers.views.MyDialog;
import com.team.witkers.views.MyEditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登陆界面
 * Created by zcf on 2016/4/15.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar loginToolbar;

    private   LinearLayout ll_root_login;
    private   View loginLayout;
//    private   KeyEditText et_username;
//    private   KeyEditText et_password;

    private   MyEditText et_username;
    private   MyEditText et_password;

    private   Button btn_login;
    private   EditText editText;
    private   TextView tv_register;
    private   ProgressDialog mDialog;
    private   MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login_2);


        initView();
        initToolBar();
        initClickListener();
    }

    private void initView(){

        ll_root_login= (LinearLayout) findViewById(R.id.ll_root_login);
        loginToolbar= (Toolbar) findViewById(R.id.loginToolbar);
        loginLayout =findViewById(R.id.login_layout);
        et_username = (MyEditText)findViewById(R.id.et_username);
        et_password = (MyEditText)findViewById(R.id.et_password);

        btn_login = (Button)findViewById(R.id.btn_login);
        editText= (EditText) findViewById(R.id.editText);
        tv_register= (TextView) findViewById(R.id.tv_register);

        myDialog=new MyDialog(this);

        //添加editText，目的在于取消username、password控件获取焦点
        editText.setFocusable(true);
        boolean bool= editText.requestFocus();
//        MyLog.i("bool:  "+bool);

    }//initView

    private void initToolBar(){
        //去掉默认标题
        loginToolbar.setTitle("");
        setSupportActionBar(loginToolbar);
    }//initToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
//           Toast.makeText(this,"back2_click",Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
//            MyLog.v("loginAct back");
            finish();
        }
        if(item.getItemId()==R.id.menu_login_item_forgetPwd){
//            Toast.makeText(this,"忘记密码",Toast.LENGTH_SHORT).show();
//            MyLog.d("忘记密码");
//            Snackbar.make(ll_root_login,"back2_click",Snackbar.LENGTH_SHORT).show();
            myDialog.setDialotTitle("忘记密码");
            myDialog.getDialog().show();


        }
        return super.onOptionsItemSelected(item);
    }

    private void initClickListener(){
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

//        et_password.setKeyPreImeListener(this);
//        et_username.setKeyPreImeListener(this);

        et_username.setMyEditTextChangeListener(new MyEditText.MyEditTextChangeListener() {
            @Override
            public void afterTextChanged1() {
                enableLoginBtn();
            }
        });
        et_password.setMyEditTextChangeListener(new MyEditText.MyEditTextChangeListener() {
            @Override
            public void afterTextChanged1() {
                enableLoginBtn();
            }
        });

    }//initEvents

    //所有的点击事件，包括tv_register
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tv_register:
//                Snackbar.make(view,"register",Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(this,RegisterActivity.class));
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
        }

    }//onCLick

    //用户登录
    private void login(){
        MyLog.d("login");
        String userName, password;
        userName = et_username.getMyText().toString();
        password = et_password.getMyText().toString();
        //信息是否完善
//        if (TextUtils.isEmpty(userName)
//                || TextUtils.isEmpty(password)) {
//            Toast.makeText(this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
//            return;
//        }

        mDialog = new ProgressDialog(LoginActivity.this, "登陆...");
        mDialog.show();
//        MyLog.i("startService之前0");
        final MyUser user = new MyUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.login(new SaveListener<Object>() {
            @Override
            public void done(Object o, BmobException e) {
                if(e==null){

//                    MyLog.i("startService之前1");
                    MyApplication.mUser=user;
                    //登录开启监听服务
                    startService(new Intent(LoginActivity.this, MyService.class));

                    mDialog.dismiss();

                    MyApplication.mUser=MyUser.getCurrentUser(MyUser.class);
                    //获取当前用户
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("LoginUser","Logined");
                    setResult(RESULT_OK,intent);
                    //加载点赞相关事件
                    loadLikeTendItems();
                    finish();
                }else{
                    mDialog.dismiss();
                    // TODO Auto-generated method stub
//                Toast.makeText(LoginActivity.this,"登录失败"+msg,Toast.LENGTH_SHORT).show();
                    myDialog.setDialotTitle("登录失败");
                    myDialog.getDialog().show();
                }//else
            }//done
        });//login

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
            enableLoginBtn();
        }
    };


    private void enableLoginBtn() {
        btn_login.setEnabled(et_username.getMyText().length() != 0 && et_password.getMyText().length() != 0);
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_CANCELED);
        finish();
//        super.onBackPressed();
    }//onBackPress

    private void loadLikeTendItems(){
        MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        // 查询用户喜欢的动态项，因此查询的是动态表
        BmobQuery<TendItems> query=new BmobQuery<>();
        query.addWhereRelatedTo("likeTendItems", new BmobPointer(myUser));
        query.findObjects(new FindListener<TendItems>() {
            @Override
            public void done(List<TendItems> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询用户喜欢动态项成功");
//                    MyLog.i("size_ "+list.size());
                    if(list.size()!=0) {//有点赞才能赋值更新
                        List<BmobObject> likeList = new ArrayList<BmobObject>();
                        for (int i = 0; i < list.size(); i++) {
                            TendItems tendNew = list.get(i);
                            tendNew.setLike(true);
                            likeList.add(tendNew);
                        }
                        setLikeItemsTrue(likeList);
                    }
                }else
                    MyLog.e("查询用户喜欢动态项失败");
            }
        });

    }
    private void setLikeItemsTrue(List<BmobObject> list){

        BmobQuery<TendItems> query=new BmobQuery<>();
        new BmobBatch().updateBatch(list).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if(e==null){
                    MyLog.i("likeTrue 修改成功");
                }else{
                    MyLog.e("likeTrue修改失败");
                }
            }//done
        });//updataBatch
    }
}//LoginAct_cls
