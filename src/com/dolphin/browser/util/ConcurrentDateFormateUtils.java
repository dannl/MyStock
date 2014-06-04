package com.dolphin.browser.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author blwang
 *
 * ConcurrentDateFormateUtils of utils
 */
public class ConcurrentDateFormateUtils {


    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATER = new ThreadLocal<SimpleDateFormat>() {

        @SuppressLint("SimpleDateFormat")
        @Override
        protected SimpleDateFormat initialValue() {

            return new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        }
    };

    public static String formateDateToString(Date date) {

        return DATE_FORMATER.get().format(date);
    }

    public static String currentDateToString() {

        return formateDateToString(new Date());
    }
}
