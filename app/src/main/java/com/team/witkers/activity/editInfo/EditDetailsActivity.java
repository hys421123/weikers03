package com.team.witkers.activity.editInfo;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class EditDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ib_editDone;
    private EditText et_info;
    private ImageView iv_topBack;
    private MyUser myUser;
    private TextView tv_TopTitle;
    private int intentFlag;
    private Handler myHandler = new Handler();


    @Override
    protected int setContentId() {
        return R.layout.activity_edit_details;
    }

    @Override
    protected void initView() {
        ib_editDone = (ImageButton) findViewById(R.id.ib_edit_done);
        et_info = (EditText) findViewById(R.id.et_info);
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tv_TopTitle = (TextView) findViewById(R.id.tv_topTitle);
    }

    @Override
    protected void initToolBar() {
        switch (intentFlag){
            case 1:
                tv_TopTitle.setText("更改昵称");
                break;
            case 2:
                tv_TopTitle.setText("更改姓名");
                break;
            case 3:
                tv_TopTitle.setText("个人简介");
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void getIntentData() {
         intentFlag = this.getIntent().getIntExtra("info",0);
        MyLog.i("intentFlag-->"+intentFlag);
    }

    @Override
    protected void setListener() {
        iv_topBack.setOnClickListener(this);
        ib_editDone.setOnClickListener(this);
    }

    @Override
    protected void showView() {
        MyUser user= BmobUser.getCurrentUser(MyUser.class);
        if(user==null){return;}
        else {
            myUser = user;
            switch (intentFlag){
                case 1:
                    et_info.setText(myUser.getUsername());
                    break;
                case 2:
                    et_info.setText(myUser.getRelName());
                    break;
                case 3:
                    et_info.setText(myUser.getIntroduce());
                    break;
                default:
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.ib_edit_done:
                MyToast.showToast(this,et_info.getText().toString());
                String info = et_info.getText().toString();
                myUser.setLocation("湖北 武汉");
                switch (intentFlag){
                    case 1:
                        String nickNameStr = info.trim();
                        if(nickNameStr!=null&&!nickNameStr.equals("")){
                            myUser.setNickName(nickNameStr);
                            pubPersonalInfo();
                        }else {
                            MyToast.showToast(this,"你的昵称不能为空，请重新输入");
                        }
                        break;
                    case 2:
                        String relNameStr = info.trim();
                        MyLog.i("reNameStr-->"+relNameStr);
                        if(relNameStr!=null&&!relNameStr.equals("")){
                            myUser.setRelName(relNameStr);
                            pubPersonalInfo();
                        }else {
                            MyToast.showToast(this,"你的姓名不能为空，请重新输入");
                        }
                        break;
                    case 3:
                        myUser.setIntroduce(info);
                        pubPersonalInfo();
                        break;
                    default:
                        finish();
                        break;
                }
        }

    }

    private void pubPersonalInfo() {

        myUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyToast.showToast(EditDetailsActivity.this,"更新成功");
                    finish();
                }else{
                    MyToast.showToast(EditDetailsActivity.this,"更新失败"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
