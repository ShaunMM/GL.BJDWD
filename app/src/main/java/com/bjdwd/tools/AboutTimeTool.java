package com.bjdwd.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dell on 2017/3/23.
 * 年 月 日 时 分 秒 ~ Y M D H S S S
 */
public class AboutTimeTool {

    public static String getYMDHSSString() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        String smin = "";
        String ssec = "";
        if (min < 10) {
            smin = "0" + min;
        } else {
            smin = min + "";
        }
        if (sec < 10) {
            ssec = "0" + sec;
        } else {
            ssec = sec + "";
        }
        String date = year + "-" + month + "-" + day + " " + hour + ":" + smin + ":" + ssec;

        return date;
    }

    public static String getMDHSString() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String smin = "";
        if (min < 10) {
            smin = "0" + min;
        } else {
            smin = min + "";
        }
        String date = month + "-" + day + " " + hour + ":" + smin;
        return date;
    }

    public static String getYMDString() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String date = year + "-" + month + "-" + day;
        return date;
    }

    public static String getYMString() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        String date = year + "-" + month;
        return date;
    }

    //将毫秒转成时间yyyy-MM-dd HH:mm
    public static String getTimeStringToLong(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = new Date(millis);
        return sdf.format(dt);
    }

}