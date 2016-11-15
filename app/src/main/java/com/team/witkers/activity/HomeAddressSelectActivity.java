package com.team.witkers.activity;

import android.content.Intent;
import android.widget.FrameLayout;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;

import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.City;
import chihane.jdaddressselector.model.County;
import chihane.jdaddressselector.model.Province;
import chihane.jdaddressselector.model.Street;

public class HomeAddressSelectActivity extends BaseActivity implements OnAddressSelectedListener {


    @Override
    protected int setContentId() {
        return R.layout.activity_home_address_select;
    }


    @Override
    protected void initView() {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        AddressSelector selector = new AddressSelector(this);
        selector.setOnAddressSelectedListener(this);
        assert frameLayout != null;
        frameLayout.addView(selector.getView());
    }

    @Override
    public void onAddressSelected(Province province, City city, County county, Street street) {
        String s =
                (province == null ? "" : province.name) +
                        (city == null ? "" : "\n" + city.name) +
                        (county == null ? "" : "\n" + county.name) +
                        (street == null ? "" : "\n" + street.name);


        MyLog.i(province.name+","+city.name+","+county.name);

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("location",s);
        setResult(RESULT_OK,intent);
        finish();
    }
}
