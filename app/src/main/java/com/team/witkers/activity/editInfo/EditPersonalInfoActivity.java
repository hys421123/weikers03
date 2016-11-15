package com.team.witkers.activity.editInfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hys.mylog.MyLog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.team.witkers.R;
import com.team.witkers.activity.CropActivity;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.CropAvatarEvent;
import com.team.witkers.utils.MyToast;
import com.team.witkers.views.ItemLongOnClickDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import chihane.jdaddressselector.BottomDialog;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.City;
import chihane.jdaddressselector.model.County;
import chihane.jdaddressselector.model.Province;
import chihane.jdaddressselector.model.Street;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class EditPersonalInfoActivity extends BaseActivity implements View.OnClickListener ,OnAddressSelectedListener{


    private static final int NICKNAME = 1;
    private static final int RELNAME = 2;
    private static final int INTRODUCE = 3;
    private static final int REQUESTCODE_PIC = 1;//打开相册
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpeg";
    private RelativeLayout rel_headIcon,rel_nickName,rel_relName,rel_sex,rel_location,rel_introduce;
    private TextView tv_nickName,tv_relName,tv_sex,tv_location,tv_introduce;
    private ImageView iv_topBack;
    private MyUser myUser;
    private Intent intentToEdit;
    private BottomDialog addressDialog;
    private com.makeramen.roundedimageview.RoundedImageView ri_headIcon;
    private Uri destinationUri;

    @Override
    protected int setContentId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    protected void initEventBus() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        rel_headIcon = (RelativeLayout) findViewById(R.id.rel_headIcon);
        rel_nickName = (RelativeLayout) findViewById(R.id.rel_nickName);
        rel_relName = (RelativeLayout) findViewById(R.id.rel_relName);
        rel_sex = (RelativeLayout) findViewById(R.id.rel_sex);
        rel_location = (RelativeLayout) findViewById(R.id.rel_location);
        rel_introduce = (RelativeLayout) findViewById(R.id.rel_introduce);
        ri_headIcon = (RoundedImageView) findViewById(R.id.ri_headIcon);
        tv_nickName = (TextView) findViewById(R.id.tv_nickName);
        tv_relName = (TextView) findViewById(R.id.tv_relName);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        destinationUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));

    }

    @Override
    protected void setListener() {
        iv_topBack.setOnClickListener(this);
        rel_headIcon.setOnClickListener(this);
        rel_nickName .setOnClickListener(this);
        rel_relName.setOnClickListener(this);
        rel_sex.setOnClickListener(this);
        rel_location.setOnClickListener(this);
        rel_introduce.setOnClickListener(this);
        ri_headIcon.setOnClickListener(this);
    }

    @Override
    protected void showView() {
        MyUser user= BmobUser.getCurrentUser(MyUser.class);
        if(user==null){return;}
        else {
            myUser = user;
            setUserHead(myUser);
            tv_nickName.setText(myUser.getUsername());
            tv_relName.setText(myUser.getRelName());
            tv_sex.setText(myUser.getSex());
            tv_location.setText(myUser.getLocation());
            String introduceStr = myUser.getIntroduce();
            tv_introduce.setText(introduceStr);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showView();//更改返回后刷新界面
//        MyLog.i("onResume---------->");
    }

    private void setUserHead(MyUser myUser){
        if(myUser!=null&&myUser.getHeadUrl()!=null){
            String headUrl=myUser.getHeadUrl();
            Glide.with(this)
                    .load(headUrl)
                    .into(ri_headIcon);
        }else{
            Glide.with(this)
                    .load(R.drawable.default_head)
                    .into(ri_headIcon);
        }
    }


    private void pubPersonalInfo() {

        myUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    if(addressDialog!=null){
                        addressDialog.dismiss();
                        showView();
                    }
                    MyToast.showToast(EditPersonalInfoActivity.this,"更新成功");
                }else{
                    MyToast.showToast(EditPersonalInfoActivity.this,"更新失败"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.rel_headIcon:
                MyLog.i("rel_headIcon");
                Intent picIntent = new Intent(Intent.ACTION_PICK,null);
                picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(picIntent,REQUESTCODE_PIC);
                break;
            case R.id.ri_headIcon:
                MyLog.i("ri_headIcon");
                new ImageViewer.Builder(this,myUser.getHeadUrl()).show();
                break;
            case R.id.rel_nickName:
                MyLog.i("rel_nickName");
                MyToast.showToast(this,"昵称不可更改哟~");
                break;
            case R.id.rel_relName:
                intentToEdit = new Intent(EditPersonalInfoActivity.this,EditDetailsActivity.class);
                intentToEdit.putExtra("info",RELNAME);
                startActivity(intentToEdit);
                break;
            case R.id.rel_sex:
                MyLog.i("rel_sex");
                ItemLongOnClickDialog.Builder builder = new ItemLongOnClickDialog.Builder(this);
                builder.setMessage1("男", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_sex.setText("男");
                        myUser.setSex("男");
                        pubPersonalInfo();
                        dialog.dismiss();
                    }
                });
                builder.setMessage2("女", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_sex.setText("女");
                        myUser.setSex("女");
                        pubPersonalInfo();
                        dialog.dismiss();
                    }
                });

                builder.create().show();
                break;
            case R.id.rel_location:
                addressDialog = new BottomDialog(EditPersonalInfoActivity.this);
                addressDialog.setOnAddressSelectedListener(EditPersonalInfoActivity.this);
                addressDialog.show();
                break;
            case R.id.rel_introduce:
                intentToEdit = new Intent(EditPersonalInfoActivity.this,EditDetailsActivity.class);
                intentToEdit.putExtra("info",INTRODUCE);
                startActivity(intentToEdit);
                break;
        }
    }

    /**
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(CropAvatarEvent event) {
        File imageFile=event.getFile();
        String userHeadUrl=event.getbFileName();
        MyLog.i("eventBus_headUrl_"+userHeadUrl);
        myUser.setHeadUrl(userHeadUrl);
        myUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                MyLog.i("图像更新成功");
                setUserHead(myUser);
            }
        });
    }

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

    @Override
    public void onAddressSelected(Province province, City city, County county, Street street) {
        String location = (province == null ? "" : province.name) +
                        (city == null ? "" : city.name) +
                        (county == null ? "" : county.name) +
                        (street == null ? "" : street.name);
        Toast.makeText(this,location, Toast.LENGTH_SHORT).show();
        myUser.setLocation(location);
        pubPersonalInfo();
    }


}
