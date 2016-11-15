package com.team.witkers.activity.find;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.team.witkers.R;
import com.team.witkers.adapter.HotLableAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.HotLable;
import com.team.witkers.bean.Lable;
import com.team.witkers.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class HotLableActivity extends BaseActivity implements OnClickListener{


    private ImageView iv_topBack;
    private TextView tv_topTitle;
    private LinearLayout lin_search_lable;
    private ListView lv_hotLable;
    private HotLableAdapter hotLableAdapter;
    private List<Lable> lableList;

    @Override
    protected int setContentId() {
        return R.layout.activity_hot_lable;
    }

    @Override
    protected void initView() {
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tv_topTitle = (TextView) findViewById(R.id.tv_topTitle);
        lin_search_lable = (LinearLayout) findViewById(R.id.lin_search_lable);
        lv_hotLable = (ListView) findViewById(R.id.lv_hotLable);
        tv_topTitle.setText("热门标签");
    }

    @Override
    protected void setListener() {
        iv_topBack.setOnClickListener(this);
        lin_search_lable.setOnClickListener(this);
    }

    @Override
    protected void showView() {
        lableList = new ArrayList<Lable>();
        BmobQuery<HotLable> bmobQuery = new BmobQuery<HotLable>();
        bmobQuery.getObject("cda2e287cb", new QueryListener<HotLable>() {
            @Override
            public void done(HotLable object,BmobException e) {
                if(e==null){
                    for(int i=0;i<object.getHotLableList().size();i++){
                        String lableTemp = object.getHotLableList().get(i);
                        Lable lable = new Lable("lable",lableTemp);
                        lableList.add(lable);
                    }
                    hotLableAdapter = new HotLableAdapter(HotLableActivity.this,lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);
                }else{

                }
            }
        });
    }



    @Override
    protected void initData() {
        lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyToast.showToast(HotLableActivity.this,"Onclick    on--"+position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.lin_search_lable:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                break;
        }
    }
}
