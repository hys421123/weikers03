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
import com.team.witkers.activity.homeitem.PersonalHomePageActivity2;
import com.team.witkers.adapter.HotLableAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.Lable;
import com.team.witkers.bean.Mission;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;
import com.team.witkers.views.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 搬家
 * Created by zcf on 2016/4/14.
 */
public class AllSearchFragment extends BaseFragment {

    private ListView lv_hotLable;
    private HotLableAdapter hotLableAdapter;
    private ClearEditText et_lable_search;
    List<Lable> lableList = new ArrayList<>();
    List<Mission> missonList = new ArrayList<>();
    //取出其中之一的标签，显示一个即可
//    List<Mission> missionList2=new ArrayList<>();
    List<MyUser> userList = new ArrayList<>();

    private static final int  FLAG = 1;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == FLAG){
                MyLog.i("执行了判断");

                if(userList.size()!=0&&missonList.size()!=0){//都不为空
                    MyLog.d("都不为空");
                    for (int i =0;i<userList.size();i++){
                        Lable lable = new Lable(userList.get(i).getHeadUrl(),userList.get(i).getUsername());
                        lableList.add(lable);
                    }
//                    for(int i=0;i<missonList.size();i++){
                        Lable lable = new Lable("mission",missonList.get(0).getCategory());
                        lableList.add(lable);
//                    }
                    hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);

                    lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        MyToast.showToast(getActivity(),position+"");
//                        MyLog.d("item click"+userList.get(position).getUsername());
                            Intent intent;
                            if(position<userList.size()) {
                                intent = new Intent(getActivity(), PersonalHomePageActivity2.class);
                                intent.putExtra("fromTakeOutMissionAdapterTV", userList.get(position));
                            }else{
                                 intent = new Intent(getActivity(), FindMissionPageActivity.class);
                                intent.putExtra("fromTakeOutMissionAdapterTV",(Serializable)missonList);
                                intent.putExtra("fromFindMissoionLabel",missonList.get(0).getCategory());
                            }

                            getActivity().startActivity(intent);
                        }
                    });
                }//都不为空


                if(userList.size()!=0&&missonList.size()==0){//只有用户不为空
//                    MyLog.d("只有用户11不为空");
                    for (int i =0;i<userList.size();i++){
                        Lable lable = new Lable(userList.get(i).getHeadUrl(),userList.get(i).getUsername());
                        lableList.add(lable);
                    }
                    hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);

                    MyLog.d("只有 用户不为空");

                    lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        MyToast.showToast(getActivity(),position+"");
//                        MyLog.d("item click"+userList.get(position).getUsername());
                        Intent intent = new Intent(getActivity(), PersonalHomePageActivity2.class);
                        intent.putExtra("fromTakeOutMissionAdapterTV",userList.get(position));
                        getActivity().startActivity(intent);
                    }
                });

                }//只有用户不为空


                if(userList.size()==0&&missonList.size()!=0){//只有任务不为空
                    MyLog.d("只有任务不为空");
//                    for(int i=0;i<missonList.size();i++){
                        Lable lable = new Lable("mission",missonList.get(0).getCategory());
                        lableList.add(lable);
//                    }
                    hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);

                    lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        MyToast.showToast(getActivity(),position+"");
                        MyLog.d("item  mission click");
                            Intent intent = new Intent(getActivity(), FindMissionPageActivity.class);
                            intent.putExtra("fromTakeOutMissionAdapterTV",(Serializable)missonList);
                            intent.putExtra("fromFindMissoionLabel",missonList.get(0).getCategory());
                            getActivity().startActivity(intent);
                        }
                    });
                }//只有用户不为空
                if(userList.size()==0&&missonList.size()==0){//都为空
                    MyLog.i("两个都为空，没有搜索内容");
                    lableList.clear();
                    hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);
                }


//                lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        MyToast.showToast(getActivity(),position+"");
//                        MyLog.d("item click");
//                    }
//                });
            }//msg.what=flag
        }
    };


    @Override
    protected int setContentId() {
        return R.layout.fragment_all_search_2;
    }

    @Override
    protected void initView(View view) {
        lv_hotLable = (ListView) view .findViewById(R.id.lv_hotLable_all);
        et_lable_search = (ClearEditText) getActivity().findViewById(R.id.et_lable_search);
        String temp = et_lable_search.getText().toString().trim();

        MyLog.i("labelSearch_ "+temp);
        if(temp!=null&&!temp.equals("")){
            searchPerson(temp);
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

                MyLog.i("temp_ "+temp);
                if(temp!=null&&!temp.equals("")){
                    searchPerson(temp);
                }else {
                    lableList.clear();
                    hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);
                }
            }
        });

    }

    public void searchPerson(final String searchTemp){
        lableList.clear();

        MyLog.d("searchName_ "+searchTemp);
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        //        注:模糊查询只对付费用户开放，付费后可直接使用。
//        query.addWhereContains("username",searchTemp);

        query.addWhereEqualTo("username", searchTemp);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        MyLog.i("没有查找到相关用户");
                    }
                        userList = list;
                    searchMission(searchTemp);
                }else{
                    MyLog.i("contains error"+e);
                }
            }
        });
    }
    protected void searchMission(String searchTemp){
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
                        Message msg = new Message();
                        msg.what = FLAG;
                        mHandler.handleMessage(msg);


                }else{
                    MyLog.i("contains error"+e);
                }
            }
        });
    }

}