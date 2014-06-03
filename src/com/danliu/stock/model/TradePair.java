/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    TradePair
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import android.text.TextUtils;

/**
 * TradePair of MyStock.
 * @author danliu
 *
 */
public class TradePair implements KLine {

    private Trade mFromTrade;
    private Trade mToTrade;

    public TradePair(final Trade from, final Trade to) {
        if (!TextUtils.equals(from.getStock().getId(), to.getStock().getId())) {
            throw new IllegalArgumentException("we can not make a trade pair between deferent stock!");
        }
        mFromTrade = from;
        mToTrade = to;
    }

    public String getStockId() {
        return mFromTrade.getStock().getId();
    }

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


}
