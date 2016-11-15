package com.team.witkers.activity.homeitem;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by jin on 2016/8/20.
 */
public  class NoUnderlineClickableSpan extends ClickableSpan {
    @Override
    public  void onClick(View widget){};

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        // 去掉超链接的下划线
        ds.setUnderlineText(false);
//        ds.setColor(ds.linkColor);
    }
}
