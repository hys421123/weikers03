package com.team.witkers.base;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by hys on 2016/7/29.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }//onCreate


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(setContentId(), container, false);

        getArguments0();
        initDataBeforeView();
        initView(view);
        setListener();
        loadDataAfterView();

        return view;
    }

    protected void getArguments0(){

    }

    @Override
    public void onResume() {
        loadDataonResume();
        super.onResume();
    }


    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        showView();
    }



    protected abstract int setContentId();
    protected void initView(View view){}
    protected void setListener(){}
    protected void initDataBeforeView(){}
    protected void loadDataAfterView(){}

    protected void showView(){}

    protected void loadDataonResume(){}
}//BaseFrm
