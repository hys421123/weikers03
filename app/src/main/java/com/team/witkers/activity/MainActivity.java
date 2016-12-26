package com.team.witkers.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hys.mylog.MyLog;
import com.team.witkers.MyApplication;
import com.team.witkers.R;
import com.team.witkers.activity.find.SearchActivity;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendItems;
import com.team.witkers.fragment.FindFragment;
import com.team.witkers.fragment.HomeFragment;
import com.team.witkers.fragment.MeFragmentLogin;
import com.team.witkers.fragment.MsgFragment;
import com.team.witkers.service.MyService;
import com.team.witkers.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //let me see
    public static final String BmobID = "01e6a3045fb017cddbc2b79ee38a705c";
    private static final int REQUESTCODE_MELOG = 1;//从Mefragment中登录进去，若返回，则至主界面
    private static final int REQUESTCODE_LOCATION = 3;
    private boolean isHomeSelected=false;
    private boolean isMeSelected=false;
//      zcf  1d40f25e125f6477590f1de699056c58
    //  weike   01e6a3045fb017cddbc2b79ee38a705c

    public AMapLocationClient mLocationClient = null;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationOption = null;
    private BmobGeoPoint myPoint;

    private Fragment mFrmHome;
    private Fragment mFrmMsg;
    private Fragment mFrmLike;
    private Fragment mFrmMe;

    private ImageButton ibtn_index_home;
    private ImageButton ibtn_index_msg;
    private ImageButton ibtn_index_like;
    private ImageButton ibtn_index_me;

    private LinearLayout mIndexLayout;
    private LinearLayout mNewsLayout;
    private LinearLayout mFindLayout;
    private LinearLayout mMineLayout;
    private LinearLayout mPubLayout;
    private MenuItem searchItem;
    private MenuItem locationItem;

    //记录Fragment的位置
    private int position = 0;

    Toolbar mToolbar;

    private final String[] mTitles = {"首页", "消息", "最爱", "我的"};
//  private  BmobGeoPoint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_2);
//        myPoint = new BmobGeoPoint(114.398331,30.506929);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 111);
        }

        MyLog.d("MainAct create");
        initToolBar();
        initView();
        initEvent();
//        setSelect(0);
        initMap();


        //加载点赞相关事件
//        loadLikeTendItems();
    }

    private void initMap() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //声明定位回调监听器
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLatitude();//获取纬度
                        aMapLocation.getLongitude();//获取经度
                        myPoint = new BmobGeoPoint(aMapLocation.getLongitude(),aMapLocation.getLatitude());
                        setSelect(0);
                    } else {
                        MyLog.i( "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
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
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void loadLikeTendItems(){
        MyUser myUser=BmobUser.getCurrentUser(MyUser.class);

        // 查询用户喜欢的动态项，因此查询的是动态表
        BmobQuery<TendItems> query=new BmobQuery<>();
        query.addWhereRelatedTo("likeTendItems", new BmobPointer(myUser));
        query.findObjects(new FindListener<TendItems>() {
            @Override
            public void done(List<TendItems> list, BmobException e) {
                if(e==null){
                    MyLog.i("查询用户喜欢动态项成功");
                    MyLog.i("size_ "+list.size());
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
    }//setLikeItems

    //初始化toolbar
    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //通过id找到控件
    private void initView() {


//        MyLog.d("Main_initView");
        mIndexLayout = (LinearLayout) findViewById(R.id.ll_tab_home);
        mNewsLayout = (LinearLayout) findViewById(R.id.ll_tab_msg);
        mFindLayout = (LinearLayout) findViewById(R.id.ll_tab_like);
        mMineLayout = (LinearLayout) findViewById(R.id.ll_tab_me);
        mPubLayout = (LinearLayout) findViewById(R.id.ll_tab_publish);
        ibtn_index_home = (ImageButton) findViewById(R.id.ibtn_index_home);
        ibtn_index_msg = (ImageButton) findViewById(R.id.ibtn_index_msg);
        ibtn_index_like = (ImageButton) findViewById(R.id.ibtn_index_like);
        ibtn_index_me = (ImageButton) findViewById(R.id.ibtn_index_me);
    }

    private void initEvent() {
        mIndexLayout.setOnClickListener(this);
        mNewsLayout.setOnClickListener(this);
        mFindLayout.setOnClickListener(this);
        mMineLayout.setOnClickListener(this);
        mPubLayout.setOnClickListener(this);
        
        if(MyUser.getCurrentUser(MyUser.class)!=null){//用户是否在线
//            MyLog.v("current_user on");
            //用户在线，即开启监听服务
            MyApplication.mUser=MyUser.getCurrentUser(MyUser.class);
            startService(new Intent(this, MyService.class));
        }else{
            MyLog.e("current_user null");
        }

//        MyLog.d("query user");
//        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
////        注:模糊查询只对付费用户开放，付费后可直接使用。
//        query.addWhereContains("username","z");
////        query.addWhereEqualTo("username", "zs");
//        query.findObjects(new FindListener<MyUser>() {
//            @Override
//            public void done(List<MyUser> list, BmobException e) {
//                if(e==null){
//                    if(list.size()==0){
//                        MyLog.i("没有查找到相关用户");
//                    }
//                    else{
//                        MyLog.d("查询成功 "+list.get(0).getHeadUrl() );
//                    }
//                }else{
//                    MyLog.i("查询失败  "+e);
//                }
//            }
//        });

    }//initEvent


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
         searchItem = menu.findItem(R.id.action_search);
//        locationItem = menu.findItem(R.id.action_location);

//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == searchItem.getItemId()) {
//            onBackPressed();
//            MyToast.showToast(this, "OnClick on the Search");
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
//        else if(id == locationItem.getItemId()){
//            MyToast.showToast(this,"Click on the location");
//            Intent intent = new Intent(this,HomeAddressSelectActivity.class);
//            startActivityForResult(intent,REQUESTCODE_LOCATION);
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 选择fragment
     *
     * @param i 位置
     */
    private void setSelect(int i) {
        resetImgs();
        this.position = i;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
//        MyLog.i("mainActivity----latitude->"+myPoint.getLatitude()+",longitude->"+myPoint.getLongitude());

        // 设置内容区域
        switch (i) {
            case 0:
                mToolbar.setTitle("微客");
                if (mFrmHome == null) {
                    mFrmHome = new HomeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("myPoint",myPoint);
                    mFrmHome.setArguments(bundle);
                    transaction.add(R.id.id_content, mFrmHome);
                } else {
                    transaction.show(mFrmHome);
                }
                ibtn_index_home.setImageResource(R.drawable.ic_index_home_selected);
                break;
            case 1:
                mToolbar.setTitle("消息");
                if (mFrmMsg == null) {
                    mFrmMsg = new MsgFragment();
                    transaction.add(R.id.id_content, mFrmMsg);
                } else {
                    transaction.show(mFrmMsg);

                }
                ibtn_index_msg.setImageResource(R.drawable.ic_index_msg_selected);
                break;
            case 2:
                mToolbar.setTitle("发现");
                if (mFrmLike == null) {
                    mFrmLike = new FindFragment();
                    transaction.add(R.id.id_content, mFrmLike);
                } else {
                    transaction.show(mFrmLike);
                }
                ibtn_index_like.setImageResource(R.drawable.ic_index_like_selected);
                break;
            case 3:
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                if (user == null) {
//                    mToolbar.setTitle("请登录");
//                    mFrmMe = new MeFragmentLogout();
//                    transaction.add(R.id.id_content, mFrmMe);
                    Intent intent=new Intent(this,LoginActivity.class);
                    startActivityForResult(intent,REQUESTCODE_MELOG);

                } else {//user not null
                    mToolbar.setTitle(user.getUsername());
                    if (mFrmMe == null) {
                        mFrmMe = new MeFragmentLogin();
                        transaction.add(R.id.id_content, mFrmMe);
                    } else {
                        transaction.show(mFrmMe);
                    }
                }//else
                ibtn_index_me.setImageResource(R.drawable.ic_index_me_selected);
                break;

            default:
                break;
        }

        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //result_ok 接收来自me_fragment启动的LoginActivity的finsh事件传递的参数
        if (resultCode == RESULT_CANCELED) {//未登陆返回 canceled

            MyLog.e("result cancel requestCode:  "+requestCode);
            switch (requestCode) {
                case REQUESTCODE_MELOG:
                        MyLog.v("requestcode MELOG onActivityResult");
                        isHomeSelected = true;
                    break;
            }//switch
        }//if


      if(resultCode==RESULT_OK){//登陆后返回
          MyLog.v("返回到 已登录");
          if(requestCode == REQUESTCODE_LOCATION){
              String s = data.getStringExtra("location");
              MyToast.showToast(this,s);
                return;
          }
          isMeSelected=true;
          //刚的登录，要重新加载点赞项目
//          loadLikeTendItems();
      }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {

        if(MyApplication.SETLOGOUT){
            setSelect(0);
            MyApplication.SETLOGOUT=false;
        }
//        MyLog.d("MainAct onResume");
        if(isHomeSelected){
//            MyLog.i("isHomeTrue");
            setSelect(0);
            isHomeSelected=false;
        }
        if(isMeSelected){
            setSelect(3);
            isMeSelected=false;
        }
        super.onResume();

    }

    /**
     * 隐藏所有的fragment
     *
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (mFrmHome != null) {
            transaction.hide(mFrmHome);
        }
        if (mFrmMsg != null) {
            transaction.hide(mFrmMsg);
        }
        if (mFrmLike != null) {
            transaction.hide(mFrmLike);
        }
        if (mFrmMe != null) {
            transaction.hide(mFrmMe);
        }
    }


    /**
     * 重置图片
     */
    private void resetImgs() {
        ibtn_index_home.setImageResource(R.drawable.ic_index_home);
        ibtn_index_msg.setImageResource(R.drawable.ic_index_msg);
        ibtn_index_like.setImageResource(R.drawable.ic_index_like);
        ibtn_index_me.setImageResource(R.drawable.ic_index_me);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_tab_home:
                setSelect(0);
                break;
            case R.id.ll_tab_msg:
                setSelect(1);
                break;
            case R.id.ll_tab_like:
                setSelect(2);
                break;
            case R.id.ll_tab_me:
                setSelect(3);
                break;
            case R.id.ll_tab_publish:

                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    //发布
                    startActivity(new Intent(MainActivity.this, PublishMissionActivity.class))
                    ;
                }


                break;

            default:
                break;
        }
    }


    //    解决Fragment重叠的两个方法onRestoreInstanceState(),onSaveInstanceState()
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        position = savedInstanceState.getInt("position");
        setSelect(position);
        super.onRestoreInstanceState(savedInstanceState);
    }

    //解决Fragment重叠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //记录当前的position
        outState.putInt("position", position);
    }

    @Override
    protected void onDestroy() {
        MyLog.i("MainAct destroy");
        super.onDestroy();
    }
}//MainAct_cls

