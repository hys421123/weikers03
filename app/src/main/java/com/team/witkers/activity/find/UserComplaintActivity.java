package com.team.witkers.activity.find;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.Advice;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class UserComplaintActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_complaint_resume,et_complaint_details;
    private Button btn_submit;
    private ImageView iv_topBack;
    private TextView tv_topTitle;
    private MyUser myUser;
    private String resumeStr;
    private String detailsStr;
    private Handler mHandler = new Handler();


    @Override
    protected int setContentId() {
        return R.layout.activity_user_complaint_2;
    }

    @Override
    protected void initView() {
        et_complaint_resume = (EditText) findViewById(R.id.et_complaint_resume);
        et_complaint_details = (EditText) findViewById(R.id.et_complaint_details);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tv_topTitle = (TextView) findViewById(R.id.tv_topTitle);
        myUser = BmobUser.getCurrentUser(MyUser.class);
        tv_topTitle.setText("意见反馈");
    }

    @Override
    protected void setListener() {
        iv_topBack.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    private Boolean checkNull(){
         resumeStr = et_complaint_resume.getText().toString().trim();
         detailsStr = et_complaint_details.getText().toString().trim();
        if((resumeStr!=null&&!resumeStr.equals(""))&&(detailsStr!=null&&!detailsStr.equals(""))){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.btn_submit:
                MyToast.showToast(this,"submit");
                if(checkNull()){
                    Advice advice = new Advice();
                    advice.setAuthor(myUser);
                    advice.setResume(resumeStr);
                    advice.setDetails(detailsStr);
                    advice.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e!=null){
                                MyToast.showToast(UserComplaintActivity.this,"上传失败");
                            }else{
                                MyToast.showToast(UserComplaintActivity.this,"上传成功");
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        et_complaint_resume.setText("");
                                        et_complaint_details.setText("");
                                    }
                                });
                            }

                        }
                    });
                }else {
                    MyToast.showToast(this,"亲，你填写的信息不能为空哦");
                }
        }

    }
}
