package com.team.witkers.fragment.homefrm;

import com.team.witkers.base.BaseFragment;

/**
 * Created by jin on 2016/9/27.
 * 用户判断哪个Fragment可见，只加载可见的Fragment的数据
 */
public class BaseFragmentForDelayLoad extends BaseFragment {


    @Override
    protected int setContentId() {
        return 0;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            onVisible();
        }else{
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        delayLoad();
    }


    /**
     * 不可见
     */
    protected void onInvisible() {


    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected  void delayLoad(){};
}
