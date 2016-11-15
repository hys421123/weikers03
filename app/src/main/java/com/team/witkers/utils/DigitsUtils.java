package com.team.witkers.utils;

import java.text.DecimalFormat;

/**
 * Created by hys on 2016/7/30.
 */
public class DigitsUtils {
    public static String setDigitsPoint(float digit){
        float chargesNum=digit;
        String chargesStr="";
        int chargesInt=(int)chargesNum;
        if(chargesInt>=1000){
            chargesStr=chargesInt+"";
        }else{
            DecimalFormat df= new DecimalFormat("######0.00");
            chargesStr=df.format(chargesNum);
        }
        return chargesStr;
    }//setDigitsPoint
}
