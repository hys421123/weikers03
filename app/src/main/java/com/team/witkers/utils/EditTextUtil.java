package com.team.witkers.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by hys on 2016/7/30.
 */
public class EditTextUtil {
    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        //there are more than 2 digits after the decimal point
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
//                    if(s.length()-1-s.toString().indexOf(".")==1){
//                        //there is only 1 digit after the point
//                        editText.setText(s+"0");
//                    }
                }
//                else {//not contains "."
//                    if(Integer.valueOf(s.toString())>999){
//                        editText.setText(s);
//                    }else{
//                        editText.setText(s+".00");
//                    }
//                }
                if (s.toString().trim().substring(0).equals(".")) {
                    //when write the only point  ".",default to add 0 before,like "0."
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
//                if (!s.toString().contains(".")) {
//                    //not contains "."
//                    if (Integer.valueOf(s.toString()) > 999) {
//                        editText.setText(s);
//                    } else {
//                        editText.setText(s + ".00");
//                    }
//                }
            }//afterTextChanged

        });

    }
}
