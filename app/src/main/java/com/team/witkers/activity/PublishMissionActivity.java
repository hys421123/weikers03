package com.team.witkers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.team.witkers.R;
import com.team.witkers.activity.address.AddressSelectActivity;
import com.team.witkers.bean.ChooseClaimant;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.eventbus.PubMissionEvent;
import com.team.witkers.utils.EditTextUtil;
import com.team.witkers.utils.MyToast;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 发布任务
 * <p/>
 * Created by zcf on 2016/4/15.
 */
public class PublishMissionActivity extends AppCompatActivity implements View.OnClickListener
        , TimePickerDialog.OnTimeSetListener {
    private EditText nameTv, phoneTv, et_charges;
    private TextInputEditText infoEt;
    private TextView timeTv,addressTv,wordsTv;
    private String name, phone, address, prices, time, info;
    private LinearLayout timeLayout,addressLayout;
    private ImageView addImg;
    private Toolbar toolbar;
    private Button pubBtn;
    private CheckBox anonymity;//匿名选择框

    public static final int REQUESTCODE_ADDRESS=0;
    public static final int REQUESTCODE_LABLE=1;

    SpannableString spannableString;

    private String dateStr;//时间传参
    private String categoryStr;//标签
    //声明时间格式化对象
    private SimpleDateFormat mFormatter = new SimpleDateFormat("aa hh:mm ");
    SlideDateTimePicker.Builder builder;
    private double latitude;
    private double longitude;
    private MyUser user;
    //地图相关

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    //handler传递时间参数，用来更新UI
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    timeTv.setText(dateStr);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(PublishMissionActivity.this, mFormatter.format(date), Toast.LENGTH_SHORT).show();

            dateStr = mFormatter.format(date);
            Character time_flag = dateStr.charAt(0);
            // Log.i("PJ","长度："+dateStr.length());

            if(time_flag.equals('下')) {
                //获得小时，并加12
                //Log.i("PJ","获得的时间为："+dateStr);
                String str_minute = dateStr.substring(5);
                String str_hour = dateStr.substring(3,5);
                int int_hour = Integer.parseInt(str_hour);
                if(int_hour!=12)
                    int_hour = int_hour + 12;
                dateStr = String.valueOf(int_hour)+str_minute;
            }else{
                String str_minute = dateStr.substring(5);
                String str_hour = dateStr.substring(3,5);
                dateStr = str_hour+str_minute;
            }

            // Log.i("PJ","dateStr"+dateStr);
            Message message = new Message();
            message.what = 0;

            myHandler.sendMessage(message);
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(PublishMissionActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task);
        initMap();
        initView();
        initEvents();
        showMsg();//自动填写姓名和手机号码
        infoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordsTv.setText(s.length()+"/200");
                Log.i("Test","s-->"+s);
                String sTemp = s.toString();
                spannableString = new SpannableString(sTemp);
                int index1 = sTemp.indexOf("#");
                if (index1!=-1){
                    int index2 = sTemp.indexOf("#", index1 + 1);
                    if(index2!=-1){
                        Log.i("Test","index-->"+index1+"--"+index2);
                        categoryStr = sTemp.substring(index1 + 1, index2);
                        Log.i("Test","categotyStr-->"+categoryStr);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("Test","afterTextChanged");

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 初始化地图
     */
    private void initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //声明定位回调监听器
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {

                        address = aMapLocation.getDistrict()+ aMapLocation.getStreet();
                        latitude = aMapLocation.getLatitude();
                        longitude = aMapLocation.getLongitude();
                        addressTv.setText(address);
                        MyToast.showToast(PublishMissionActivity.this,address);
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("hys", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                        MyToast.showToast(PublishMissionActivity.this,getString(R.string.location_faild));
                    }
                }
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        nameTv = (EditText) findViewById(R.id.id_pubName);
        phoneTv = (EditText) findViewById(R.id.id_pubPhone);
        addressTv = (TextView) findViewById(R.id.id_pubAddress);
        wordsTv = (TextView) findViewById(R.id.tv_words);

        et_charges = (EditText) findViewById(R.id.et_charges);
        EditTextUtil.setPricePoint(et_charges);
        infoEt = (TextInputEditText) findViewById(R.id.id_pub_needText);
        timeTv = (TextView) findViewById(R.id.id_pubTime);
        timeLayout = (LinearLayout) findViewById(R.id.id_pubTimeLayout);
        addressLayout = (LinearLayout) findViewById(R.id.id_pubAddressLayout);//地址的布局对象
        addImg = (ImageView) findViewById(R.id.id_pubImg);
        pubBtn = (Button) findViewById(R.id.id_pubBtn);


          toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


    }

    private void initEvents() {
//        iv_back.setOnClickListener(this);
        addImg.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        addressLayout.setOnClickListener(this);
        pubBtn.setOnClickListener(this);
    }

    /**
     * 自动填写默认信息 比如名字 电话
     */
    private void showMsg() {
        user = BmobUser.getCurrentUser(MyUser.class);
        nameTv.setText(user.getUsername());
        phoneTv.setText(user.getMobilePhoneNumber());
    }

    //检测信息是否为空
    private boolean checkInfo() {
        name = nameTv.getText().toString().trim();
        phone = phoneTv.getText().toString().trim();
        prices = et_charges.getText().toString().trim();
        address = addressTv.getText().toString().trim();
        time = timeTv.getText().toString();
        info = infoEt.getText().toString().trim();


//        MyLog.d("price--"+prices);

        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(prices)
                || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(info)) {
            MyToast.showToast(PublishMissionActivity.this,"请完善信息");
            return false;
        }

        if(info!=null&&!info.equals("")){
            if(info.indexOf("#") ==-1){
                MyToast.showToast(PublishMissionActivity.this,"请完善标签");
                return false;
            }else if(info.indexOf("#")!=-1&&info.indexOf("#",info.indexOf("#")+1)==-1){
                MyToast.showToast(PublishMissionActivity.this,"请完善标签");
                return false;
            }else if(info.indexOf("#")!=-1 &&info.indexOf("#",info.indexOf("#")+1)!=-1){
                int index2 = info.indexOf("#",info.indexOf("#")+1);
                String infoElse = info.substring(index2);
                if(infoElse==null||infoElse.equals("")){//只有标签，没有内容
                    MyToast.showToast(PublishMissionActivity.this,"请完善内容");
                    return false;
                }
            }
        }
        return true;
    }

    //发布信息到服务器
    private void pubMsg() {
        Mission mission = new Mission();
        mission.setName(name);
        mission.setPhone(phone);
        mission.setAddress(address);
        mission.setFinishTime(time);
        mission.setCharges(Float.valueOf(prices));
        mission.setPubUser(user);
        mission.setInfo(info);
        mission.setPubUserName(user.getUsername());
        mission.setPubUserHeadUrl(user.getHeadUrl());
        Log.i("test","categotyStr-mission->"+categoryStr);
        mission.setCategory(categoryStr);
        mission.setClaimItemList(null);
        mission.setLatitude(latitude);
        mission.setLongitude(longitude);
        mission.setMissionLocation(new BmobGeoPoint(longitude,latitude));

        ///////////
//        ChooseClaimant claimant=new ChooseClaimant();
//        mission.setChooseClaimant(claimant);

        final ProgressDialog dialog = new ProgressDialog(this, "发布中...");
        dialog.show();
        mission.save(new SaveListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e==null){
                    MyToast.showToast(PublishMissionActivity.this,"发布成功");
                    //通知更新数据
                    EventBus.getDefault().post(new PubMissionEvent());
                    dialog.dismiss();
                    finish();
                }else{
                    MyToast.showToast(PublishMissionActivity.this,"发布失败" + e.getMessage());
                    dialog.dismiss();
                }//else
            }
        });


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_pubTimeLayout:
                builder = new SlideDateTimePicker.Builder(getSupportFragmentManager());
                builder.setIs24HourTime(true);
                builder .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                break;
            case R.id.id_pubImg:
                Intent intent2 = new Intent(PublishMissionActivity.this,CategorySelectActivity.class);
                startActivityForResult(intent2,REQUESTCODE_LABLE);
                break;
            case R.id.id_pubBtn:
                if (checkInfo()){
                    pubMsg();
                }

                break;
            case R.id.id_pubAddressLayout:
                Intent intent = new Intent(PublishMissionActivity.this,AddressSelectActivity.class);
                intent.putExtra("fromPublishMissionActivity",address);
                startActivityForResult(intent,REQUESTCODE_ADDRESS);
                Log.i("Test","点击了地址文本框");
                break;
            default:
                Log.i("Test","点击无效");
                break;
        }
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second){
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;

        String time = hourString + ":" + minuteString;
        timeTv.setText(time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mlocationClient.stopLocation();//停止定位
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUESTCODE_ADDRESS){
            if(data!=null) {
                String value_address = data.getExtras().getString("fromAddressSelectActivity");
                Log.i("Test", "收到了传回的地址");
                addressTv.setText(value_address);
                latitude = data.getDoubleExtra("latitude",0);
                longitude = data.getDoubleExtra("longitude",0);

            }else{
                return;
            }//else
        }else if(requestCode == REQUESTCODE_LABLE){//已经选好 标签了
            if(data!=null) {
                String LableSelectStr =data.getExtras().getString("fromCategorySelect");
                String str=infoEt.getText().toString();
                infoEt.setText(str+"#"+LableSelectStr+"#");
            }else{
                return;
            }//else
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
