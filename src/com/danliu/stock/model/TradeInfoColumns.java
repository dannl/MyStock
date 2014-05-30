/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    TradeInfoColumns
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 30, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import android.provider.BaseColumns;

/**
 * TradeInfoColumns of MyStock.
 * @author danliu
 *
 */
public class TradeInfoColumns implements BaseColumns {

    public static final String COLUMN_STOCK_ID = "stock_id";

    public static final String COLUMN_STOCK_NAME = "stock_name";

    public static final String COLUMN_TRADE_DATE = "date";

    public static final String COLUMN_TRADE_PRICE = "price";

    public static final String COLUMN_TRADE_COUNT = "count";

    public static final String COLUMN_TRADE_AMOUNT = "amount";

    public static final String COLUMN_BALANCE = "balance";

    public static final String TABEL_NAME = "trade_info";

    public static final String DEFAULT_SORT_ORDER = COLUMN_TRADE_DATE + " ASC";

}
