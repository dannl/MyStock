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

import com.danliu.stock.model.Trade.TradeType;
import android.text.TextUtils;

/**
 * TradePair of MyStock.
 * @author danliu
 *
 */
public class TradePair implements KLine {

    private Trade mFromTrade;
    private Trade mToTrade;
    private long mFromDateNum;
    private long mToDateNum;

    public TradePair(final Trade from, final Trade to) {
        if (!TextUtils.equals(from.getStock().getId(), to.getStock().getId())) {
            throw new IllegalArgumentException("we can not make a trade pair between deferent stock!");
        }
        mFromTrade = from;
        mToTrade = to;
        mFromDateNum = mFromTrade.getTradeDate().getDateNumber();
        mToDateNum = mToTrade.getTradeDate().getDateNumber();
    }

    public String getStockId() {
        return mFromTrade.getStock().getId();
    }

    @Override
    public float getMaxPrice(Date date) {
        if (isDateInRange(date)) {
            return getTradeCount() * getStock().getMaxPrice(date) - getStartingTradeAmount();
        }
        return 0;
    }

    @Override
    public float getMinPrice(Date date) {
        if (isDateInRange(date)) {
            return getTradeCount()  * getStock().getMinPrice(date) - getStartingTradeAmount();
        }
        return 0;
    }

    @Override
    public float getOpenPrice(Date date) {
        if (isDateInRange(date)) {
            if (date == mFromTrade.getTradeDate()) {
                return 0;
            } else {
                return getTradeCount() * getStock().getOpenPrice(date) - getStartingTradeAmount();
            }
        }
        return 0;
    }

    @Override
    public float getClosePrice(Date date) {
        if (isDateInRange(date)) {
            if (date == mToTrade.getTradeDate() && !(mToTrade.getTradeType() == TradeType.NOT_TRADED)) {
                return Math.abs(mToTrade.getTradeAmount()) - getStartingTradeAmount();
            } else {
                return  getTradeCount() * getStock().getClosePrice(date) - getStartingTradeAmount();
            }
        }
        return 0;
    }

    private float getTradeCount() {
        return mFromTrade.getTradeCount();
    }


    @Override
    public Date fromDate() {
        return mFromTrade.getTradeDate();
    }

    @Override
    public Date toDate() {
        return mToTrade.getTradeDate();
    }

    private boolean isDateInRange(final Date date) {
        final long dateNum = date.getDateNumber();
        return dateNum <= mToDateNum && dateNum >= mFromDateNum;
    }

    private float getStartingTradeAmount() {
        return Math.abs(mFromTrade.getTradeAmount());
    }

    @Override
    public Stock getStock() {
        return mFromTrade.getStock();
    }

    public void refreshPrices() {
        mFromTrade.getStock().refreshPrices();
    }

}
