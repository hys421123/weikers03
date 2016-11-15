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
import com.team.witkers.activity.homeitem.PersonalHomePageActivity2;
import com.team.witkers.adapter.HotLableAdapter;
import com.team.witkers.base.BaseFragment;
import com.team.witkers.bean.Lable;
import com.team.witkers.bean.MyUser;
import com.team.witkers.utils.MyToast;
import com.team.witkers.views.ClearEditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by hys on 2016/8/22.
 */
public class UserSearchFragment extends BaseFragment {

    private ListView lv_hotLable;
    private HotLableAdapter hotLableAdapter;
    private List<Lable> lableList = new ArrayList<>();
    private ClearEditText et_lable_search;
    List<MyUser> userList = new ArrayList<>();
    private static final int  FLAG = 3;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == FLAG){
//                MyLog.i("执行了判断");
                if(userList.size()!=0){
                    for(int i=0;i<userList.size();i++){
                        Lable lable = new Lable(userList.get(i).getHeadUrl(),userList.get(i).getUsername());
                        lableList.add(lable);
                    }
                    hotLableAdapter = new HotLableAdapter(getActivity(),lableList);
                    lv_hotLable.setAdapter(hotLableAdapter);

                    lv_hotLable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            MyToast.showToast(getActivity(),position+"");
//                        MyLog.d("item click"+userList.get(position).getUsername());
                            Intent intent = new Intent(getActivity(), PersonalHomePageActivity2.class);
                            intent.putExtra("fromTakeOutMissionAdapterTV",userList.get(position));
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
        return R.layout.fragment_user_search;
    }

    @Override
    protected void initView(View view) {
        et_lable_search = (ClearEditText) getActivity().findViewById(R.id.et_lable_search);
        lv_hotLable = (ListView) view.findViewById(R.id.lv_hotLable_user);

        String temp = et_lable_search.getText().toString().trim();
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
                if(temp!=null&&!temp.equals("")){
                    searchPerson(temp);
                }
            }
        });
    }

    protected void searchPerson(String searchTemp){
        lableList.clear();
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("username",searchTemp);
        query.findObjects(new FindListener<MyUser>() {

            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        MyLog.i("没有查找到相关用户");
                    }
                    userList = list;
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
