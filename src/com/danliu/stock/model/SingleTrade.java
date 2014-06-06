/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    SingleTrade
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

/**
 * SingleTrade of MyStock.
 * @author danliu
 *
 */
public class SingleTrade implements KLine {

    private Trade mTrade;

    public SingleTrade(final Trade trade) {
        mTrade = trade;
    }

    @Override
    public float getMaxPrice(Date date) {
        if (date.equals(mTrade.getTradeDate())) {
            return mTrade.getTradeAmount();
        } else {
            return 0;
        }
    }

    @Override
    public float getMinPrice(Date date) {
        if (date.equals(mTrade.getTradeDate())) {
            return mTrade.getTradeAmount();
        } else {
            return 0;
        }
    }

    @Override
    public float getOpenPrice(Date date) {
        if (date.equals(mTrade.getTradeDate())) {
            return mTrade.getTradeAmount();
        } else {
            return 0;
        }
    }

    @Override
    public float getClosePrice(Date date) {
        if (date.equals(mTrade.getTradeDate())) {
            return mTrade.getTradeAmount();
        } else {
            return 0;
        }
    }

    @Override
    public Date fromDate() {
        return mTrade.getTradeDate();
    }

    @Override
    public Date toDate() {
        return mTrade.getTradeDate();
    }

    @Override
    public Stock getStock() {
        return mTrade.getStock();
    }

    @Override
    public void refreshPrices() {
    }

}
