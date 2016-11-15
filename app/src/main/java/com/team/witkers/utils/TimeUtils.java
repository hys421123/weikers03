package com.team.witkers.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zcf on 2016/4/17.
 */
public class TimeUtils {
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_IMG_DATE = new SimpleDateFormat("yyyyMMdd_hhmmss");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis timeInMillis
     * @param dateFormat   dateFormat
     * @return String
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * 获取系统时间 格式 20160101_112233
     *
     * @return
     */

    public static String getTimeForImg() {
        return  getTime(getCurrentTimeInLong(), DATE_IMG_DATE);

    }


    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis time
     * @return String
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return long
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return String
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @param dateFormat dateFormat
     * @return String
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {

        return getTime(getCurrentTimeInLong(), dateFormat);
    }
}
