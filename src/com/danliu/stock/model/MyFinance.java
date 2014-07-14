/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    MyFinance
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 4, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import com.danliu.stock.model.Trade.TradeType;
import com.danliu.stock.trade.util.TradeManager;
import com.danliu.stock.util.Constants;
import com.danliu.util.Log;

/**
 * MyFinance of MyStock.
 *
 * @author danliu
 *
 */
public class MyFinance implements KLine {

    // private List<KLine> mKLines;
    private List<Trade> mAllTrades;

    public MyFinance() {
    }

    private Date mFromDate;
    @Override
    public Date fromDate() {
        if (mFromDate == null) {
            mFromDate = Date.parseDateFromNumber(Constants.STARTING_DATE);

        }
        return mFromDate;
    }

    @Override
    public Date toDate() {
        return Date.parseDateFromCalendar(Calendar.getInstance());
    }

    @Override
    public Stock getStock() {
        return Stock.MY_FINACE;
    }

    public void loadTrades() {
        mAllTrades = TradeManager.getInstance().getAllTrades();
    }

    @Override
    public void refreshPrices() {
        // final List<KLine> kLines = mKLines;
        // for (KLine kLine : kLines) {
        // kLine.refreshPrices();
        // }
        final List<Trade> allTrades = mAllTrades;
        for (Trade trade : allTrades) {
            trade.getStock().refreshPrices();
        }
    }

    @Override
    public float getClosePrice(Date date) {
        final HashMap<Stock, Integer> stockCounts = getStockCountsInDate(date);
        Set<Entry<Stock, Integer>> entries = stockCounts.entrySet();
        float result = getBalanceInDate(date);
        for (Entry<Stock, Integer> entry : entries) {
            final Stock stock = entry.getKey();
            final int count = entry.getValue();
            result += count * stock.getClosePrice(date);
        }
        return result;
    }

    @Override
    public float getOpenPrice(Date date) {
        final HashMap<Stock, Integer> stockCounts = getStockCountsInDate(date);
        Set<Entry<Stock, Integer>> entries = stockCounts.entrySet();
        float result = getBalanceInDate(date);
        for (Entry<Stock, Integer> entry : entries) {
            final Stock stock = entry.getKey();
            final int count = entry.getValue();
            result += count * stock.getOpenPrice(date);
        }
        return result;
    }

    @Override
    public float getMaxPrice(Date date) {
        final HashMap<Stock, Integer> stockCounts = getStockCountsInDate(date);
        Set<Entry<Stock, Integer>> entries = stockCounts.entrySet();
        float result = getBalanceInDate(date);
        for (Entry<Stock, Integer> entry : entries) {
            final Stock stock = entry.getKey();
            final int count = entry.getValue();
            result += count * stock.getMaxPrice(date);
        }
        return result;
    }

    @Override
    public float getMinPrice(Date date) {
        final HashMap<Stock, Integer> stockCounts = getStockCountsInDate(date);
        Set<Entry<Stock, Integer>> entries = stockCounts.entrySet();
        float result = getBalanceInDate(date);
        for (Entry<Stock, Integer> entry : entries) {
            final Stock stock = entry.getKey();
            final int count = entry.getValue();
            result += count * stock.getMinPrice(date);
        }
        return result;
    }

    private HashMap<Stock, Integer> getStockCountsInDate(Date date) {
        final HashMap<Stock, Integer> stockCounts = new HashMap<Stock, Integer>();
        final List<Trade> allTrades = mAllTrades;
        final int size = allTrades.size();
        for (int i = 0; i < size; i++) {
            final Trade trade = allTrades.get(i);
            TradeType tradeType = trade.getTradeType();
            if (trade.getTradeDate().compareTo(date) > 0
                    || tradeType == TradeType.TRANSFER_IN
                    || tradeType == TradeType.TRANSFER_OUT
                    || tradeType == TradeType.NONE) {
                continue;
            }
            final Stock stock = trade.getStock();
            int count = 0;
            if (stockCounts.containsKey(stock)) {
                count = stockCounts.get(stock);
            }
            count += trade.getTradeCount();
            stockCounts.put(stock, count);
        }
        return stockCounts;
    }

    private float getBalanceInDate(final Date date) {
        final List<Trade> allTrades = mAllTrades;
        final int size = allTrades.size();
        float balance = 0;
        for (int i = size - 1; i >= 0; i--) {
            final Trade trade = allTrades.get(i);
            if (trade.getTradeDate().compareTo(date) < 0) {
                balance = trade.getBalance();
                break;
            }
        }
        return balance;
    }

    // private float getCapitalInDate(Date date, final List<KLine> kLines) {
    // final int size = kLines.size();
    // float capital = 0;
    // for (int i = 0; i < size; i++) {
    // final KLine kLine = kLines.get(i);
    // if (kLine instanceof SingleTrade && kLine.fromDate().getDateNumber() <=
    // date.getDateNumber()) {
    // capital += kLine.getOpenPrice(kLine.fromDate());
    // } else if (kLine instanceof TradePair && kLine.toDate().getDateNumber() <
    // date.getDateNumber()) {
    // capital += ((TradePair) kLine).getDletaPrice();
    // }
    // }
    // return capital;
    // }

    // public List<KLine> getKLines() {
    // return mKLines;
    // }

}
