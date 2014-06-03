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

/**
 * Date of MyStock.
 * @author danliu
 *
 */
public class Date implements Comparable<Date> {

    private long mRealDate;
    private long mDate;

    public Date(final long date) {
        mRealDate = date;
        mDate = date / 1000000;
    }

    public long getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return String.valueOf(mDate);
    }

    @Override
    public int compareTo(Date another) {
        return (int) (mRealDate - another.mRealDate);
    }

}
