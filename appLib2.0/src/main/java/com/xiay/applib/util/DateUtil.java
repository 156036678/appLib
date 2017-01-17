package com.xiay.applib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *@author  Xiay
 */
public class DateUtil {
    /**
     * 英文简写（默认）如：2010-12-01
     */
    public static final String FORMAT_SHORT = "yyyy-MM-dd";
    /**
     * 英文全称 如：2010-12-01 23:15:06
     */
    public static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    /**
     * 英文全称 如：2010-12-01 23:15
     */
    public static final String FORMAT_MIDDLE = "yyyy-MM-dd HH:mm";

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
 /**获取时间的时间戳
  * */
    public static long getTimestamp(String time, String pattern){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern);
        Date date= null;
        try {
            date = simpleDateFormat .parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
