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
        return mTrade.getTradeAmount();
    }

    @Override
    public float getMinPrice(Date date) {
        return mTrade.getTradeAmount();
    }

    @Override
    public float getOpenPrice(Date date) {
        return mTrade.getTradeAmount();
    }

    @Override
    public float getClosePrice(Date date) {
        return mTrade.getTradeAmount();
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
        return mTrade.getStock();
    }

}
