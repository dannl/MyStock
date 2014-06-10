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

import android.R.string;
import android.util.Log;

import com.danliu.stock.trade.util.StockPriceRefresher;
import com.danliu.stock.util.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Stock of MyStock.
 *
 * @author danliu
 */
public class Stock implements KLine {

    public static final Stock BANK_OPERATION = new CustomStock("-1", "Bank Operation");
    public static final Stock MY_FINACE = new CustomStock("-2", "My Finance");
    private String mId;
    private String mName;
    private String mPrefixForGoogle;
    private String mPrefixForSina;
    private String mPrefixForYahoo;
    private List<StockPrice> mStockPrices;
    private List<StockPrice> mHistoricalPrices;
    private StockPrice mCurrentPrice;

    protected Stock(String id, String name) {
        mId = id;
        mName = name;
        if (id.startsWith("60")) {
            mPrefixForGoogle = Constants.SH_FOR_GOOGLE;
            mPrefixForSina = Constants.SH_FOR_SINA;
            mPrefixForYahoo = Constants.SH_FOR_YAHOO;
        } else {
            mPrefixForGoogle = Constants.SZ_FOR_GOOGLE;
            mPrefixForSina = Constants.SZ_FOR_SINA;
            mPrefixForYahoo = Constants.SZ_FOR_YAHOO;
        }
    }

    public String getPrefixForYahoo() {
        return mPrefixForYahoo;
    }

    public String getPrefixForGoogle() {
        return mPrefixForGoogle;
    }

    public String getPrefixForSina() {
        return mPrefixForSina;
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

    public boolean needUpdateHistoricalPrices() {
        return !(mHistoricalPrices != null && mHistoricalPrices.size() > 0);
    }

    public void setHistoricalPrice(final List<StockPrice> prices) {
        if (!needUpdateHistoricalPrices()) {
            return;
        }
        mHistoricalPrices = prices;
        if (mStockPrices == null) {
            mStockPrices = new ArrayList<StockPrice>(prices.size() + 1);
        }
        Log.d("TEST", "set historical price for " + this);
        mStockPrices.addAll(prices);
    }

    public void updateCurrentPrice(final StockPrice price) {
        if (mCurrentPrice == null) {
            mCurrentPrice = price;
            if (mStockPrices == null) {
                mStockPrices = new ArrayList<StockPrice>();
                mStockPrices.add(price);
            } else {
                mStockPrices.add(0, price);
            }
        } else {
            mCurrentPrice.set(price);
        }
    }

    @Override
    public Stock getStock() {
        return this;
    }

    @Override
    public float getMaxPrice(Date date) {
        final StockPrice price = getPriceAt(date);
        if (price == null) {
            return 0;
        }
        return price.getMaxPrice();
    }

    @Override
    public float getMinPrice(Date date) {
        final StockPrice price = getPriceAt(date);
        if (price == null) {
            return 0;
        }
        return price.getMinPrice();
    }

    @Override
    public float getOpenPrice(Date date) {
        final StockPrice price = getPriceAt(date);
        if (price == null) {
            return 0;
        }
        return price.getOpenPrice();
    }

    @Override
    public float getClosePrice(Date date) {
        final StockPrice price = getPriceAt(date);
        if (price == null) {
            return 0;
        }
        return price.getClosePrice();
    }

    private StockPrice getPriceAt(Date date) {
        final List<StockPrice> prices = mStockPrices;
        if (prices == null || prices.isEmpty()) {
            return null;
        }
        for (StockPrice stockPrice : prices) {
            if (stockPrice.getDate().equals(date)) {
                return stockPrice;
            }
        }
        return null;
    }

    @Override
    public Date fromDate() {
        if (mStockPrices == null || mStockPrices.isEmpty()) {
            return null;
        } else {
            return mStockPrices.get(mStockPrices.size() - 1).getDate();
        }
    }

    @Override
    public Date toDate() {
        if (mStockPrices == null || mStockPrices.isEmpty()) {
            return null;
        } else {
            return mStockPrices.get(0).getDate();
        }
    }

    @Override
    public void refreshPrices() {
        StockPriceRefresher.getInstance().refreshStockPrice(this);
    }

    private static final HashMap<String, Stock> STOCK_CACHE = new HashMap<String, Stock>();
    public static final Stock createStock(final String id, final String name) {
        if (STOCK_CACHE.containsKey(id)) {
            return STOCK_CACHE.get(id);
        } else {
            final Stock stock = new Stock(id, name);
            STOCK_CACHE.put(id, stock);
            return stock;
        }
    }

    public static Stock createCustomStock(String id, String name) {
        if (STOCK_CACHE.containsKey(id)) {
            return STOCK_CACHE.get(id);
        } else {
            final Stock stock = new CustomStock(id, name);
            STOCK_CACHE.put(id, stock);
            return stock;
        }
    }
}
