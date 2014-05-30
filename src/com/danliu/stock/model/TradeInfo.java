/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    TradeInfo
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 30, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import android.database.Cursor;
import android.text.TextUtils;

/**
 * TradeInfo of MyStock.
 * @author danliu
 *
 */
public class TradeInfo {

    private Stock mStock;
    private long mTradeDate;
    private float mTradePrice;
    private float mTradeCount;
    private float mTradeAmount;
    private float mBalance;

    public static final TradeInfo parseCursorAtIndex(final Cursor cursor, final int index) {
        return null;
    }

    public static final TradeInfo parseFileLine(final String tradeString) {
        if (TextUtils.isEmpty(tradeString)) {
            return null;
        }
        return null;
    }
}
