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

/**
 * Stock of MyStock.
 * @author danliu
 *
 */
public class Stock {

    public static final Stock BANK_OPERATION = new Stock("-1", "Bank Operation");
    private String mId;
    private String mName;

    public Stock(String id, String name) {
        mId = id;
        mName = name;
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
