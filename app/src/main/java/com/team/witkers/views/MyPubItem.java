package com.team.witkers.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.witkers.R;


/**
 * Created by hys on 2016/8/3.
 */
public class MyPubItem extends RelativeLayout {

    //view 内小组件
    private RelativeLayout rl_mypub_item;
    private ImageView iv_item_lefticon, iv_item_righticon;
    private TextView tv_item_title;

    //对应属性值 attrs
    private String title;
    private float titleTextSize;
    private int titleColor;
    private Drawable leftIcon;
    private Drawable rightIcon;
    private boolean rightIconVisibility,pubRightIconClickable;



    public MyPubItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view= View.inflate(context, R.layout.custom_mypub_item,this);
        rl_mypub_item = (RelativeLayout) view.findViewById(R.id.rl_mypub_item);
        iv_item_lefticon = (ImageView) view.findViewById(R.id.iv_item_lefticon);
        iv_item_righticon = (ImageView) view.findViewById(R.id.iv_item_righticon);
        tv_item_title = (TextView) view.findViewById(R.id.tv_item_title);

        //获取出自定义属性
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.MyPubItem);
        title=typedArray.getString(R.styleable.MyPubItem_myPubTitle);
        titleColor = typedArray.getColor(R.styleable.MyPubItem_myPubTitleTextColor,0);
        titleTextSize = typedArray.getDimension(R.styleable.MyPubItem_myPubTitleTextSize,0);

        leftIcon=typedArray.getDrawable(R.styleable.MyPubItem_leftIcon);
        rightIcon=typedArray.getDrawable(R.styleable.MyPubItem_rightIcon);
        rightIconVisibility=typedArray.getBoolean(R.styleable.MyPubItem_rightIconVisibility,true);

        pubRightIconClickable=typedArray.getBoolean(R.styleable.MyPubItem_pubRightIconClickable,false);
        typedArray.recycle();

        if(title!=null)//非空时才设置title
            tv_item_title.setText(title);
        if(leftIcon!=null)
           iv_item_lefticon.setImageDrawable(leftIcon);
//
        if(rightIconVisibility==false)
            iv_item_righticon.setVisibility(INVISIBLE);
        else{
            iv_item_righticon.setVisibility(VISIBLE);
            if(rightIcon!=null)
                iv_item_righticon.setImageDrawable(rightIcon);
        }


    if(pubRightIconClickable){
        iv_item_righticon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myPubRightIconClickListener!=null)
                    myPubRightIconClickListener.onRightIconClick();
            }
        });
    }


    }//MyPubItem_constructor


    private MyPubRightIconClickListener myPubRightIconClickListener;
    public interface MyPubRightIconClickListener{
        public void onRightIconClick();
    }



    public void setMyPubRightIconClickListener(MyPubRightIconClickListener myPubRightIconClickListener){
        this.myPubRightIconClickListener=myPubRightIconClickListener;
    }
}//MyPubItem_cls
