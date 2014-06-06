/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    PriceManager
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 6, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.trade.util;

import com.danliu.stock.model.Stock;
import com.danliu.stock.model.StockPrice;
import com.danliu.stock.util.AppContext;
import android.content.Context;
import android.os.AsyncTask;
import java.util.HashMap;
import java.util.List;

/**
 * PriceManager of MyStock.
 * @author danliu
 *
 */
public class StockPriceRefresher {

    private static StockPriceRefresher sInstance;

    public static final StockPriceRefresher getInstance() {
        if (sInstance == null) {
            sInstance = new StockPriceRefresher(AppContext.getInstance());
        }
        return sInstance;
    }

    private Context mContext;
    private StockPriceRefresher(final Context context) {
        mContext = context;
    }

    public void refreshStockPrice(final Stock stock) {
        final List<StockPrice> historicalPrices = YahooPriceManager.getInstance().getPrices(stock);
        final StockPrice currentPrice = SinaPriceManager.getInstance().getPrice(stock);
        stock.updateCurrentPrice(currentPrice);
        stock.setHistoricalPrice(historicalPrices);
    }





}
