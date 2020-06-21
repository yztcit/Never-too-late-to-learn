package com.nttn.coolandroid.custom.calender;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static int compareTo(String dateStr){
        if (TextUtils.isEmpty(dateStr))
            throw new NullPointerException("dateStr cannot be null!");
        if (dateStr.length() < 10) {
            dateStr += "-00";
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        Date now = calendar.getTime();

        String[] s = dateStr.split("-");
        calendar.set(Integer.parseInt(s[0]), Integer.parseInt(s[1]) - 1, Integer.parseInt(s[2]),
                0, 0, 0);
        Date date = calendar.getTime();
        return date.compareTo(now);
}
}
