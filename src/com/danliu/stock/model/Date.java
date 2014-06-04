/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    Date
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Date of MyStock.
 * @author danliu
 *
 */
public class Date implements Comparable<Date> {

    private static final HashMap<String, Integer> MONTH = new HashMap<String, Integer>();

     static {
         MONTH.put("Jan", 1);
         MONTH.put("Feb", 2);
         MONTH.put("Mar", 3);
         MONTH.put("Apr", 4);
         MONTH.put("May", 5);
         MONTH.put("Jun", 6);
         MONTH.put("Jul", 7);
         MONTH.put("Aug", 8);
         MONTH.put("Sep", 9);
         MONTH.put("Oct", 10);
         MONTH.put("Nov", 11);
         MONTH.put("Dec", 12);
     }


    private long mRealDate;
    private long mDate;

    public Date(final long date) {
        mRealDate = date;
        mDate = date / 1000000;
    }

    private Date() {
    }

    public long getDate() {
        return mDate;
    }

    public int getYear() {
        return (int) (mDate / 10000);
    }

    public int getMonth() {
        return (int) (mDate % 10000 / 100);
    }

    public int getDay() {
        return (int) (mDate % 1000000);
    }

    @Override
    public String toString() {
        return String.valueOf(mDate);
    }

    @Override
    public int compareTo(Date another) {
        return (int) (mRealDate - another.mRealDate);
    }

    public static final Date parseDateFromCalendar(final Calendar calendar) {
        final Date date = new Date();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        date.mDate = year * 10000 + month * 100 + day;
        date.mRealDate = date.mDate * 1000000;
        return date;
    }

    /**
     * Jun 3, 2014 like this.
     */
    public static final Date parseDateFromGoogleString(final String dateString) {
        final Date date = new Date();
        final String[] splited = dateString.split("\\s+");
        int month = MONTH.get(splited[0]);
        int day = Integer.parseInt(splited[1].substring(0, splited[1].lastIndexOf(",")));
        int year = Integer.parseInt(splited[2]);
        date.mDate = year * 10000 + month * 100 + day;
        date.mRealDate = date.mDate * 1000000;
        return date;
    }

}
