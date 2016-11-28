package com.team.witkers.activity.address;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddressSelectActivity extends AppCompatActivity {

    //hehe
    private static final int REQUESTCODE  = 3;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private String address;
    private String detailAddress;
    private TextView tv_repositioning;
    private ImageButton ib_address_done;

    private EditText et_address_search;
    private EditText et_address_detailAddress;
    private TextView tv_address_tip;
    private MyListView address_listView;
    private TextView tv_address_clear;
    private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);;
    private SQLiteDatabase db;
    private BaseAdapter adapter;
    private String value_address;

    private Handler myHandler = new Handler();
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_select_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initView();
        //获得定位信息并展示在EditText中
        Intent intent_address_get = getIntent();
        value_address = intent_address_get.getStringExtra("fromPublishMissionActivity");
        et_address_search.setText(value_address);

//        et_address_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyLog.i("Click on this ");
//                Intent intent = new Intent(AddressSelectActivity.this,MapLocation.class);
//                startActivityForResult(intent,REQUESTCODE);
//            }
//        });

        // 清空搜索历史
        tv_address_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
                queryData("");
            }
        });

        ib_address_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存

                Log.i("Test","点击了对勾");
                address=et_address_search.getText().toString().trim();
                detailAddress = et_address_detailAddress.getText().toString().trim();
                if(address.equals("")) {
                    toast("你的输入为空，请重新输入！");
                    return;
                }
                else{
                    boolean hasData = hasData(address);
                    if (!hasData) {
                        String data = address;
                        Log.i("Test", "data-->" + data);
                        insertData(data);
                        queryData("");

                    }else{
                        Log.i("Test","重复地址，将不进行数据保存！");
                    }
                    Toast.makeText(AddressSelectActivity.this, "clicked!" + address+detailAddress, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddressSelectActivity.this, MainActivity.class);
                    String value_address = address+detailAddress;
                    intent.putExtra("fromAddressSelectActivity", value_address);
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);

                    setResult(RESULT_OK, intent);
                    /*updateData();*/
                    finish();
                }

                // TODO 根据输入的内容模糊查询商品，并跳转到另一个界面，由你自己去实现

            }
        });


/*
        // 定位按钮点击事件
        tv_repositioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Test","address-->");
                Toast.makeText(AddressSelectActivity.this,"开始重新定位",Toast.LENGTH_LONG).show();
                //TODO 将定位获得的值，放入EditText中显示出来
  */
/*                PublishMissionActivity pmActivity = new PublishMissionActivity();
               String address2 = pmActivity.initMap();
               Log.i("Test","address-->"+address2);
                et_address_search.setText(address2);*//*

                String address2 = initMap();
                Log.i("Test","address-->"+address2);
                if(address2==null){
                    address2 = initMap();
                    Log.i("Test","address-->"+address2);
                    et_address_search.setText(address2);
                }else{
                    et_address_search.setText(address2);
                }
            }
        });
*/

        // 搜索框的文本变化实时监听
        et_address_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    tv_address_tip.setText("常用地址");
                } else {
                    tv_address_tip.setText("常用地址");
                }

                String tempName = et_address_search.getText().toString();
                // 根据tempName去模糊查询数据库中有没有数据
                queryData(tempName);

            }
        });

        address_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String name = textView.getText().toString();
                et_address_search.setText(name);
                Toast.makeText(AddressSelectActivity.this, name, Toast.LENGTH_SHORT).show();
                // TODO 获取到item上面的文字，根据该关键字跳转到另一个页面查询，由你自己去实现
            }
        });

        queryData("");
    }

    private void initView() {
        et_address_search = (EditText) findViewById(R.id.et_address_search);
        et_address_detailAddress = (EditText) findViewById(R.id.et_address_detailAddress);
        tv_address_tip = (TextView) findViewById(R.id.tv_address_tip);
        address_listView = (MyListView) findViewById(R.id.address_listView);
        tv_address_clear = (TextView) findViewById(R.id.tv_address_clear);
        tv_repositioning = (TextView) findViewById(R.id.tv_repositioning);
        ib_address_done = (ImageButton) findViewById(R.id.ib_address_done);
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        // 创建adapter适配器对象
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[] { "name" },
                new int[] { android.R.id.text1 }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        address_listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

/*    private void updateData(){
        Cursor cursor = helper.getReadableDatabase().query(table, columns, selection, selectionArgs, null, null, null);

        new BmobObject().insertBatch(this,db, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("成功啦啦啦");
            }

            @Override
            public void onFailure(int i, String s) {
                toast("失败了");
            }
        });
    }*/




    /**
     * 初始化地图
     */
    String initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //声明定位回调监听器
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        aMapLocation.getLatitude();//获取纬度
                        aMapLocation.getLongitude();//获取经度
                        aMapLocation.getAccuracy();//获取精度信息
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);//定位时间
                        aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        aMapLocation.getCountry();//国家信息
                        aMapLocation.getProvince();//省信息
                        address = aMapLocation.getDistrict()+ aMapLocation.getStreet()+aMapLocation.getStreetNum();
                        aMapLocation.getStreetNum();//街道门牌号信息
                        aMapLocation.getCityCode();//城市编码
                        aMapLocation.getAdCode();//地区编码
                        aMapLocation.getAoiName();//获取当前定位点的AOI信息
                        /*addressTv.setText(address);
                        toast(address);
                        Log.d("hys","Amap"+address);*/
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("hys", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                        toast(getString(R.string.location_faild));
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
        return address;
    }


    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){

            Toast.makeText(this,"back2_click",Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.i("hys","destroy");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        MyLog.i("onActivityResult");
        if(requestCode == REQUESTCODE){
            MyLog.i("result is OK");
            final String title =   data.getStringExtra("location");
             latitude = data.getDoubleExtra("latitude",0);
             longitude = data.getDoubleExtra("longitude",0);
            MyLog.i("title-"+title+",latitude-"+latitude+",longitude:"+longitude);

            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    et_address_search.setText(title);
                }
            });
        }
    }
}
