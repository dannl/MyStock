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

import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Date of MyStock.
 * @author danliu
 *
 */
public class Date implements Comparable<Date> {

    /**
     * FACTOR
     */
    private static final int FACTOR = 1000000;
    private static final HashMap<String, Integer> MONTH = new HashMap<String, Integer>();
    private static final LongSparseArray<Date> DATE_CACHE = new LongSparseArray<Date>();

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

    private Date() {
    }

    public long getDateNumber() {
        return mDate;
    }

    public int year() {
        return (int) (mDate / 10000);
    }

    public int month() {
        return (int) (mDate % 10000 / 100);
    }

    public int day() {
        return (int) (mDate % 100);
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
        date.mRealDate = date.mDate * FACTOR;
        return getCachedDate(date);
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
        date.mRealDate = date.mDate * FACTOR;
        return getCachedDate(date);
    }

    /**
     * 2014-06-04
     */
    public static final Date parseDateFromYahooStr(String dateString) {
        final Date date = new Date();
        dateString = dateString.replaceAll("-", "");
        date.mDate = Long.parseLong(dateString);
        date.mRealDate = date.mDate * FACTOR;
        return getCachedDate(date);
    }

    public static final Date parseDateFromNumber(final long dateNumber) {
        final Date date = new Date();
        if (dateNumber / 10000000000000l > 0) {
            date.mRealDate = dateNumber;
            date.mDate = dateNumber / FACTOR;
        } else if (dateNumber / 10000000 > 0) {
            date.mDate = dateNumber;
            date.mRealDate = dateNumber * FACTOR;
        } else {
            throw new IllegalArgumentException("bad format of number.");
        }
        return getCachedDate(date);
    }

    public Date yesterday() {
        int day = day();
        int month = month();
        int year = year();
        if (day == 1) {
            if (month == 1) {
                year -=1;
                month = 12;
            } else {
                month -= 1;
                if (month == 2) {
                    if ((year - 1988) %4 == 0) {
                        day = 29;
                    } else {
                        day = 28;
                    }
                } else if (month % 2 == 0) {
                    if (month < 8) {
                        day = 30;
                    } else {
                        day = 31;
                    }
                } else {
                    if (month < 7) {
                        day = 31;
                    } else {
                        day = 30;
                    }
                }
            }
        } else {
            day -= 1;
        }
        final long dateNum = year * 10000 + month * 100 + day;
        if (DATE_CACHE.get(dateNum) == null) {
            final Date date = new Date();
            date.mDate = dateNum;
            date.mRealDate = dateNum * FACTOR;
            DATE_CACHE.put(dateNum, date);
            return date;
        } else {
            return DATE_CACHE.get(dateNum);
        }
    }

    private static final Date getCachedDate(final Date date) {
        long dateNumber = date.getDateNumber();
        if (DATE_CACHE.get(dateNumber) == null) {
            DATE_CACHE.put(dateNumber, date);
            return date;
        } else {
            return DATE_CACHE.get(dateNumber);
        }
    }

}
