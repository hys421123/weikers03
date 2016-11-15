package com.team.witkers.activity.address;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.ListViewAdapter;
import com.team.witkers.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;

public class MapLocation extends AppCompatActivity implements LocationSource,
        AMapLocationListener,AMap.OnCameraChangeListener,PoiSearch.OnPoiSearchListener {
    private MapView mapView;
    private ListView lv_loacation;
    private List<LocationBean> locationBeanList;
    private ListViewAdapter listViewAdapter;
    private AMap aMap;
    private UiSettings mUiSettings;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private double lat;
    private double lon;

    private PoiSearch poiSearch;
    private PoiSearch.Query query;// Poi查询条件类
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiResult poiResult; // poi返回的结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map_location);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        showView();
    }

    protected void initView() {
        lv_loacation = (ListView) findViewById(R.id.lv_location);

        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        locationBeanList = new ArrayList<LocationBean>();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    protected void showView() {
        aMap.setOnCameraChangeListener(this);
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
        aMap.setLocationSource(this);// 设置定位监听
        mUiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled((true));// 是否可触发定位并显示定位
        mUiSettings.setScrollGesturesEnabled(true);//设置地图是否可以手势滑动
        mUiSettings.setZoomGesturesEnabled(true);//设置地图是否可以手势缩放大小
        mUiSettings.setScaleControlsEnabled(true);//地图标尺是否显示

//        for(int i =0;i<20;i++){
//            LocationBean locationBean = new LocationBean("title"+i,"content"+i,R.drawable.img_location_blue);
//            locationBeanList.add(locationBean);
//        }

        lv_loacation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyLog.i("click on position--"+position);
                Intent intent = new Intent(MapLocation.this,AddressSelectActivity.class);
                intent.putExtra("location",locationBeanList.get(position).getTitle());
                intent.putExtra("latitude",locationBeanList.get(position).getLatitude());
                intent.putExtra("longitude",locationBeanList.get(position).getLongitude());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


    //定位成功后回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                lat = amapLocation.getLatitude();
                lon = amapLocation.getLongitude();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lon));
                markerOptions.title("当前位置");
                markerOptions.visible(true);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_mylocation));
                markerOptions.icon(bitmapDescriptor);
                aMap.addMarker(markerOptions);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("test",errText);
            }
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setInterval(60000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
//        LatLng target = cameraPosition.target;
//        Log.i("test",target.latitude + "running------" + target.longitude);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLng target = cameraPosition.target;
        Log.i("test",target.latitude + "-----finish------" + target.longitude);
        LatLonPoint lp = new LatLonPoint(target.latitude, target.longitude);
        doSearchQuery(lp);
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(LatLonPoint lp) {
        currentPage = 0;
        String keyWord = "商务住宅|地名地址信息|政府机构及社会团体|住宿服务";
        //|汽车销售|"汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|\n" +
//                "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|\n" +
//                "金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        query = new PoiSearch.Query(keyWord, "", null);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 2000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    public void onPoiSearched(PoiResult result, int rCode) {

//        String adName = result.getPois().get(0).getAdName();
//        String adCity = result.getPois().get(0).getCityCode();
        Log.i("test","result-->"+result.getPois().size()+",rCode-->"+rCode);

        if(rCode == 1000){
            if(result!= null){//&&result.getQuery()!= null
                locationBeanList.clear();
                List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                for(int i =0;i<poiItems.size();i++){
//                    Log.i("test",poiItems.get(i).getAdCode());
                    Log.i("test",poiItems.get(i).getBusinessArea());
                    Log.i("test",poiItems.get(i).getCityName());
//                    Log.i("test",poiItems.get(i).getDirection());
                    Log.i("test",poiItems.get(i).getProvinceName());
                    Log.i("test",poiItems.get(i).getSnippet());
                    Log.i("test",poiItems.get(i).getLatLonPoint().getLatitude()+"");
                    Log.i("test",poiItems.get(i).getLatLonPoint().getLongitude()+"");
                    Log.i("test","---------------------------------");
                    String ProvinceName = poiItems.get(i).getProvinceName();
                    String CityName = poiItems.get(i).getCityName();
                    String Snippet = poiItems.get(i).getSnippet();
                    String content = ProvinceName+CityName+Snippet;

                    double latitude = poiItems.get(i).getLatLonPoint().getLatitude();
                    double longitude = poiItems.get(i).getLatLonPoint().getLongitude();
                    LocationBean locationBean = new LocationBean(poiItems.get(i).getTitle(),content,R.drawable.img_location_blue,latitude,longitude);
                    locationBeanList.add(locationBean);
                }
                listViewAdapter = new ListViewAdapter(this,locationBeanList);
                lv_loacation.setAdapter(listViewAdapter);
            }
        }
//        dissmissProgressDialog();// 隐藏对话框
/*        if (rCode == 1000) {

            if (result != null && result.getQuery() != null) {// 搜索poi的结果

                if (result.getQuery().equals(query)) {// 是否是同一条

                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    List<SuggestionCity> suggestionCities = poiResult
                          .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if(poiItems.size()>=0)
                    for(int i=0;i<poiItems.size();i++){
                        Log.i("test",poiItems.get(i).getAdName());
                    }


                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        Log.i("test","suggestionCities.size()>0");
//                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(MainActivity.this, "没有结果",Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "没有结果",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "错误："+rCode,Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}

