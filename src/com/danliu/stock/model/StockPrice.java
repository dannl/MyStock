/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    StockPrice
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import org.json.JSONObject;

/**
 * StockPrice of MyStock.
 * @author danliu
 *
 */
public class StockPrice implements KLine {

    @Override
    public float getMaxPrice(Date date) {
        return 0;
    }

    @Override
    public float getMinPrice(Date date) {
        return 0;
    }

    @Override
    public float getOpenPrice(Date date) {
        return 0;
    }

    @Override
    public float getClosePrice(Date date) {
        return 0;
    }

    @Override
    public Date fromDate() {
        return null;
    }

    @Override
    public Date toDate() {
        return null;
    }

    @Override
    public Stock getStock() {
        return null;
    }

    public static StockPrice parseJson(Stock stock, JSONObject obj) {
        return null;
    }

}
