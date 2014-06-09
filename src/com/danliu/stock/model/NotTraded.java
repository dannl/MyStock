/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    NotTraded
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 6, 2014
 *    @version: 1.0
 *
 ******************************************************************************/

package com.danliu.stock.model;

import java.util.Calendar;

/**
 * NotTraded of MyStock.
 *
 * @author danliu
 */
public class NotTraded extends Trade {

    protected NotTraded(Stock stock) {
        super(stock);
    }

    @Override
    public float getBalance() {
        throw new IllegalAccessError("not supported.");
    }

    @Override
    public float getTradeAmount() {
        throw new IllegalAccessError("not supported.");
    }

    @Override
    public int getTradeCount() {
        throw new IllegalAccessError("not supported.");
    }

    @Override
    public Date getTradeDate() {
        return Date.parseDateFromCalendar(Calendar.getInstance());
    }

    @Override
    public float getTradePrice() {
        throw new IllegalAccessError("not supported.");
    }

    @Override
    public TradeType getTradeType() {
        return TradeType.NOT_TRADED;
    }

}
