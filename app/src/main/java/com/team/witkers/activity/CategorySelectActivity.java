package com.team.witkers.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.HotLableAdapter;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.HotLable;
import com.team.witkers.bean.Lable;
import com.team.witkers.views.ClearEditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CategorySelectActivity extends BaseActivity implements View.OnClickListener{

    private ImageView iv_topBack;
    private TextView tv_topTitle;
    private HotLableAdapter hotLableAdapter;
    private List<Lable> lableList;
    private List<String> lableNameList;
    private ListView lv_hotLable;
    private ClearEditText et_search_hotLable;

    @Override
    protected int setContentId() {
        return R.layout.activity_category_select_2;
    }

    @Override
    protected void initView() {
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tv_topTitle = (TextView) findViewById(R.id.tv_topTitle);
        tv_topTitle.setText("分类选择");
        lv_hotLable = (ListView) findViewById(R.id.lv_hotLable);
        et_search_hotLable = (ClearEditText) findViewById(R.id.et_search_hotLable);
        lableNameList = new ArrayList<String>();
    }

    @Override
    protected void setListener() {
        iv_topBack.setOnClickListener(this);
    }

    @Override
    protected void showView() {
        firstLoadData();

        et_search_hotLable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String sTemp = s.toString().trim();
                MyLog.v("onTextChanged_ "+sTemp);
                if(sTemp ==null||sTemp.equals("")){
                    firstLoadData();
                    return;
                }
                    lableList.clear();
                    Lable lable = new Lable("lable",sTemp);
                    lableList.add(lable);
//                    hotLableAdapter = new HotLableAdapter(CategorySelectActivity.this,lableList);
                    hotLableAdapter.notifyDataSetChanged();


            }//onTextChagned

            @Override
            public void afterTextChanged(Editable s) {
//                String sTemp = s.toString().trim();
//                if(sTemp ==null||sTemp.equals("")){
//                    firstLoadData();
//                    return;
//                }
//                String flag = isContent(sTemp);
//                if(flag==null){
//                    //如果其中不存在这个标签
//                    lableList.clear();
//                    Lable lable = new Lable("lable",sTemp);
//                    lableList.add(lable);
//                    hotLableAdapter = new HotLableAdapter(CategorySelectActivity.this,lableList);
//                    hotLableAdapter.notifyDataSetChanged();
//                }else{
//                    lableList.clear();
//                    Lable lable = new Lable("lable",flag);
//                    lableList.add(lable);
//                    hotLableAdapter = new HotLableAdapter(CategorySelectActivity.this,lableList);
//                    hotLableAdapter.notifyDataSetChanged();
//                }
            }//afterTextChanged
        });

        lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendIntent(lableList.get(position).getLableName());
            }
        });
    }

    private String isContent(String s){
        for(int i=0;i<lableNameList.size();i++){
            if(lableNameList.get(i).equals(s)){
                return s;
            }
        }
        return null;
    }


    private void firstLoadData() {
        lableList = new ArrayList<Lable>();
        BmobQuery<HotLable> bmobQuery = new BmobQuery<HotLable>();
        bmobQuery.getObject("cda2e287cb", new QueryListener<HotLable>() {
            @Override
            public void done(HotLable object,BmobException e) {
                if(e==null){
                    for(int i=0;i<object.getHotLableList().size();i++){
                        String lableTemp = object.getHotLableList().get(i);
                        lableNameList.add(lableTemp);
                        Lable lable = new Lable("lable",lableTemp);
                        lableList.add(lable);
                    }
                    hotLableAdapter = new HotLableAdapter(CategorySelectActivity.this,lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);
                }else{
                }
            }
        });
    }

    private void sendIntent(String extra){
        Intent intent = new Intent(this,PublishMissionActivity.class);
        intent.putExtra("fromCategorySelect",extra);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
        }
    }



}
