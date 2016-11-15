package com.team.witkers.fragment.searchfrm;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.activity.FindMissionPageActivity;
import com.team.witkers.adapter.HotLableAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.Lable;
import com.team.witkers.bean.Mission;
import com.team.witkers.views.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 租车
 * Created by zcf on 2016/4/14.
 */
public class MissionSearchFragment extends BaseFragment {

    private ListView lv_hotLable;
    private HotLableAdapter hotLableAdapter;
    private List<Lable> lableList = new ArrayList<>();
    private ClearEditText et_lable_search;
    List<Mission> missonList = new ArrayList<>();
    private static final int  FLAG = 2;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == FLAG){
//                MyLog.i("执行了判断lableSearch");
               if(missonList!=null){
//                   for(int i=0;i<missonList.size();i++){
                       Lable lable = new Lable("mission",missonList.get(0).getCategory());
                       lableList.add(lable);
//                   }
                   hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                   lv_hotLable.setAdapter(hotLableAdapter);
                   lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        MyToast.showToast(getActivity(),position+"");
                           MyLog.d("item  mission click");
                           Intent intent = new Intent(getActivity(), FindMissionPageActivity.class);
//                            missonList.get(0);
//                            intent.putExtra("fromTakeOutMissionAdapterTV",userList.get(position));
                           intent.putExtra("fromFindMissoionLabel",missonList.get(0).getCategory());
                           intent.putExtra("fromTakeOutMissionAdapterTV",(Serializable)missonList);
                           getActivity().startActivity(intent);
                       }
                   });

               }else{
                   lableList.clear();
                   hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                   lv_hotLable.setAdapter(hotLableAdapter);
               }
            }
        }
    };

    @Override
    protected int setContentId() {
        return R.layout.fragment_lable_search;
    }

    @Override
    protected void initView(View view) {
        et_lable_search = (ClearEditText) getActivity().findViewById(R.id.et_lable_search);
        lv_hotLable = (ListView) view.findViewById(R.id.lv_hotLable_lable);

        String temp = et_lable_search.getText().toString().trim();
        if(temp!=null&&!temp.equals("")){
            searchMission(temp);
        }
    }
    @Override
    protected void loadDataAfterView() {
        et_lable_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.i("s-->"+s);
                String temp = et_lable_search.getText().toString().trim();
                if(temp!=null&&!temp.equals("")){
                    searchMission(temp);
                }
            }
        });
    }

    protected void searchMission(String searchTemp){
        lableList.clear();
        BmobQuery<Mission> query = new BmobQuery<Mission>();
        query.addWhereEqualTo("category",searchTemp);
        query.findObjects(new FindListener<Mission>() {

            @Override
            public void done(List<Mission> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        MyLog.i("没有查找到相关任务");
                    }
                    missonList = list;
                    Message msg =new Message();
                    msg.what = FLAG;
                    mHandler.handleMessage(msg);
                }else{
                    MyLog.i("contains error"+e);
                }
            }
        });
    }
}