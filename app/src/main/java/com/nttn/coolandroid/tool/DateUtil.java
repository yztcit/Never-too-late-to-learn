package com.nttn.coolandroid.tool;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Apple on 2020/6/11.
 * Desc: add some description
 */
class DateUtil {
    private static final String YMDHMS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 当地时间 ---> UTC时间
     * @return
     */
    public static String Local2UTC(){
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHMS_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = sdf.format(new Date());
        return gmtTime;
    }

    /**
     * UTC时间 ---> 当地时间
     * @param utcTime   UTC时间
     * @return
     */
    public static String utc2Local(String utcTime) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(YMDHMS_FORMAT);//UTC时间格式
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(YMDHMS_FORMAT);//当地时间格式
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

    public static String utc2Local(long utcTime) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(YMDHMS_FORMAT);//UTC时间格式
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTimeString = utcFormater.format(new Date(utcTime));
        Date gpsUTCDate = utcFormater.parse(utcTimeString, new ParsePosition(0));
        SimpleDateFormat localFormater = new SimpleDateFormat(YMDHMS_FORMAT);//当地时间格式
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

    public static String longToString(long longNum, String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = YMDHMS_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date date = new Date(longNum);
        return format.format(date);
    }
}
