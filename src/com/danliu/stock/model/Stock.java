/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    Stock
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 30, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import com.danliu.stock.util.Constants;

/**
 * Stock of MyStock.
 * @author danliu
 *
 */
public class Stock {

    public static final Stock BANK_OPERATION = new Stock("-1", "Bank Operation");
    public static final Stock MY_FINACE = new Stock("-2", "My Finance");
    private String mId;
    private String mName;
    private String mPrefixForGoogle;
    private String mPrefixForSina;

    public Stock(String id, String name) {
        mId = id;
        mName = name;
        if (id.startsWith("60")) {
            mPrefixForGoogle = Constants.SH_FOR_GOOGLE;
            mPrefixForSina = Constants.SH_FOR_SINA;
        } else {
            mPrefixForGoogle = Constants.SZ_FOR_GOOGLE;
            mPrefixForSina = Constants.SZ_FOR_SINA;
        }
    }

    public String getPrefixForGoogle() {
        return mPrefixForGoogle;
    }

    public String getPrefixForSina() {
        return mPrefixForSina;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return " id: " + mId + " name: " + mName;
    }

}
