package com.team.witkers.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.hys.mylog.MyLog;
import com.team.witkers.R;

/**
 * Created by hys on 2016/8/8.
 */
public class MyEditText extends LinearLayout {

    private ImageView iv_drawable;
    private EditText et_text;
    private View view_line ;
    private Drawable drawable1,drawable;

    private String hint;
    private Drawable leftDrawable,leftDrawableBlue;
    private boolean isChangeListener,isInputPwd;

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = View.inflate(context, R.layout.custom_myedittext, this);
        iv_drawable = (ImageView) view.findViewById(R.id.iv_drawable);
        et_text = (EditText) view.findViewById(R.id.et_text);
        view_line = view.findViewById(R.id.view_line);

        //获取出自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
        hint = typedArray.getString(R.styleable.MyEditText_hint);
        leftDrawable = typedArray.getDrawable(R.styleable.MyEditText_leftDrawable);
        leftDrawableBlue = typedArray.getDrawable(R.styleable.MyEditText_leftDrawableBlue);
        isChangeListener = typedArray.getBoolean(R.styleable.MyEditText_isTextChangeListener, false);
        isInputPwd = typedArray.getBoolean(R.styleable.MyEditText_isInputPwd, false);
        typedArray.recycle();

        if (hint != null)
            et_text.setHint(hint);

        if (leftDrawable != null) {
//            MyLog.v("drawable not null");
            iv_drawable.setImageDrawable(leftDrawable);
        }
        if (isChangeListener) {
            et_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (myEditTextChangeListener != null)
                        myEditTextChangeListener.afterTextChanged1();
                }
            });
        }//isChangeListener  if

        if (isInputPwd) {//若为输入密码，则是true
//            MyLog.v("inputPwd");
            et_text.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    
           


      et_text.setOnFocusChangeListener(new OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {
//              MyLog.v("editText focus change");
              if(b) {//focused
                  view_line.setBackgroundColor(getResources().getColor(R.color.login_blue_toolbar));
                  if(leftDrawableBlue!=null) {
//                      MyLog.v("drawable blue not null");
                      iv_drawable.setImageDrawable(leftDrawableBlue);
                  }//内if
              }
              else {
                  view_line.setBackgroundColor(getResources().getColor(R.color.gray_dark));
                  if(leftDrawable!=null)
                     iv_drawable.setImageDrawable(leftDrawable);
              }//else

          }//onFocusChange
      });//  setOnFocusChangeListener

    }//MyEditText constructor


    private MyEditTextChangeListener  myEditTextChangeListener;
    public interface  MyEditTextChangeListener{
        public void afterTextChanged1();
    }

    public void setMyEditTextChangeListener( MyEditTextChangeListener  myEditTextChangeListener){
        this. myEditTextChangeListener= myEditTextChangeListener;
    }

    public Editable getMyText(){
        return et_text.getText();
    }

}//MyEditText_cls
