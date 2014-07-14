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
import java.util.IllegalFormatCodePointException;

/**
 * Date of MyStock.
 *
 * @author danliu
 */
public class Date implements Comparable<Date> {

    private static final int[] MONTH_DAYS_FLAT = new int[] {
            0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };
    private static final int[] MONTH_DAYS_NONE_FLAT = new int[] {
            0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

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
        if (calendar.get(Calendar.HOUR_OF_DAY) < 9) {
            return date.yesterday();
        }
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
                year -= 1;
                month = 12;
            } else {
                month -= 1;
                day = getMonthDay(month, year);
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

    public static boolean isValidateDate(long i) {
        Date date = Date.parseDateFromNumber(i);
        int day = date.day();
        int month = date.month();
        int year = date.year();
        if (year < 1900 || year > 2100) {
            return false;
        }
        if (month < 0 || month > 12) {
            return false;
        }
        int maxDay = 0;
        maxDay = getMonthDay(month, year);
        if (day <= 0 || day > maxDay) {
            return false;
        }
        return true;
    }

    private static int getMonthDay(int month, int year) {
        int maxDay;
        if (month == 2) {
            if ((year - 1988) % 4 == 0) {
                maxDay = 29;
            } else {
                maxDay = 28;
            }
        } else if (month % 2 == 0) {
            if (month < 8) {
                maxDay = 30;
            } else {
                maxDay = 31;
            }
        } else {
            if (month < 7) {
                maxDay = 31;
            } else {
                maxDay = 30;
            }
        }
        return maxDay;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Date)) {
            return false;
        }
        if (mDate == ((Date) o).mDate) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) mDate;
    }

    public Date daysBefore(int deltaDay) {
        int day = day();
        int month = month();
        int year = year();
        while (deltaDay > 0) {
            if (deltaDay >= day) {
                deltaDay -= day;
                month--;
                if (month == 0) {
                    month = 12;
                    year--;
                }
                day = getMonthDay(day, year);
            } else {
                day -= deltaDay;
                deltaDay = 0;
            }
        }
        return Date.parseDateFromNumber(year * 10000 + month * 100 + day);

    }

    public static int deltaDay(Date fromDate, Date date) {
        if (fromDate.getDateNumber() > date.getDateNumber()) {
            throw new IllegalArgumentException("bad params,");
        }
        int day = fromDate.day();
        int month = fromDate.month();
        int year = fromDate.year();
        int toDay = date.day();
        int toMonth = date.month();
        int toYear = date.year();
        int deltaDay = 0;
        while (day == toDay && year == toYear && month == toMonth) {
            if (toYear - year > 1) {
                year++;
                if (month <= 2) {
                    if ((year - 1 - 1988) % 4 == 0) {
                        deltaDay += 366;
                    } else {
                        deltaDay += 365;
                    }
                } else {
                    if ((year - 1988) % 4 == 0) {
                        deltaDay += 366;
                    } else {
                        deltaDay += 365;
                    }
                }
            } else if (toYear - year == 1) {
                deltaDay += restDaysOfYear(year, month, day);
                year++;
                month = 1;
                day = 1;
            } else {
                int[] monthDays;
                if ((year - 1988) % 4 == 0) {
                    monthDays = MONTH_DAYS_NONE_FLAT;
                } else {
                    monthDays = MONTH_DAYS_FLAT;
                }
                for (int i = month; i < toMonth; i++) {
                    deltaDay += monthDays[i];
                }
                deltaDay += toDay;
                deltaDay -= day;
                month = toMonth;
                day = toDay;
            }
        }
        return deltaDay;
    }

    private static int restDaysOfYear(int year, int month, int day) {
        int[] monthDays;
        if ((year - 1988) % 4 == 0) {
            monthDays = MONTH_DAYS_NONE_FLAT;
        } else {
            monthDays = MONTH_DAYS_FLAT;
        }
        int days = 0;
        for (int i = month; i < monthDays.length; i++) {
            days += monthDays[i];
        }
        days -= day;
        return days;
    }

    public void moveToLatest() {
        mRealDate = mDate * FACTOR + FACTOR - 1;
    }

    public void moveToEarliest() {
        mRealDate = mDate * FACTOR;
    }

}
