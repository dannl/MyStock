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
 * @author danliu
 *
 */
public class MyFinance implements KLine {

    private List<KLine> mKLines;

    public MyFinance() {
    }

    public void loadKLines() {
        mKLines = new ArrayList<KLine>();
        final List<Trade> allTrades = TradeManager.getInstance().getAllTrades();
        final HashMap<String, List<Trade>> tradesMap = new HashMap<String, List<Trade>>();
        for (Trade trade : allTrades) {
            final String stockId = trade.getStock().getId();
            List<Trade> stockTrades = tradesMap.get(stockId);
            if (stockTrades == null) {
                stockTrades = new ArrayList<Trade>();
                tradesMap.put(stockId, stockTrades);
            }
            stockTrades.add(trade);
        }
        final Set<Entry<String, List<Trade>>> stockTrades = tradesMap.entrySet();
        for (Entry<String, List<Trade>> entry : stockTrades) {
            final String stockId = entry.getKey();
            final List<Trade> trades = entry.getValue();
            if (Integer.parseInt(stockId) <= 0) {
                for (Trade trade : trades) {
                    mKLines.add(new SingleTrade(trade));
                }
                continue;
            }
            final List<Trade> buys = new ArrayList<Trade>();
            final List<Trade> sells = new ArrayList<Trade>();
            final List<Trade> countChanges = new ArrayList<Trade>();
            for (int i = 0; i < trades.size(); i++) {
                final Trade trade = trades.get(i);
                if (trade.getTradeType() == TradeType.BUY) {
                    buys.add(trade);
                } else if (trade.getTradeType() == TradeType.SELL) {
                    sells.add(trade);
                } else {
                    if (trade.getTradeType() == TradeType.STOCK_COUNT_CHANGE) {
                        countChanges.add(trade);
                    } else {
                        mKLines.add(new SingleTrade(trade));
                    }
                }
            }
            Trade.stockCountChangeToBuySellPair(countChanges, buys, sells);
            final int size = buys.size();
            for (int i = 0; i < size; i++) {
                Trade buy = buys.get(i);
                List<Trade> indexToRemove = new ArrayList<Trade>(sells.size());
                for (int j = 0; j < sells.size(); j++) {
                    final Trade sell = sells.get(j);
                    if (sell.getTradeDate().getDateNumber() >= buy.getTradeDate().getDateNumber()) {
                        if (sell.getTradeCount() > buy.getTradeCount()) {
                            final Trade seperatedSell = sell.seperate(buy.getTradeCount());
                            final TradePair pair = new TradePair(buy, seperatedSell);
                            mKLines.add(pair);
                            buy = null;
                            break;
                        } else if (sell.getTradeCount() < buy.getTradeCount()) {
                            final Trade seperatedBuy = buy.seperate(sell.getTradeCount());
                            final TradePair pair = new TradePair(seperatedBuy, sell);
                            mKLines.add(pair);
                            indexToRemove.add(sell);
                        } else {
                            final TradePair pair = new TradePair(buy, sell);
                            mKLines.add(pair);
                            buy = null;
                            break;
                        }
                    }
                }
                for (Trade trade : indexToRemove) {
                    sells.remove(trade);
                }
                if (buy != null) {
                    mKLines.add(new TradePair(buy, new NotTraded(buy.getStock())));
                }
            }
        }

    }

    @Override
    public float getMaxPrice(Date date) {
        final List<KLine> kLines = mKLines;
        float capital = getCapitalInDate(date, kLines);
        for (KLine kLine : kLines) {
            if (kLine instanceof TradePair) {
                final float openP = kLine.getMaxPrice(date);
                if (openP != 0f) {
                    Log.d("TEST", "plus maxp: " + kLine.getStock().getName() + " amount: " + openP);
                }
                capital += kLine.getMaxPrice(date);
            }
        }
        return capital;
    }

    @Override
    public float getMinPrice(Date date) {
        final List<KLine> kLines = mKLines;
        float capital = getCapitalInDate(date, kLines);
        for (KLine kLine : kLines) {
            if (kLine instanceof TradePair) {
                final float openP = kLine.getMinPrice(date);
                if (openP != 0f) {
                    Log.d("TEST", "plus maxp: " + kLine.getStock().getName() + " amount: " + openP);
                }
                capital += kLine.getMinPrice(date);
            }
        }
        return capital;
    }

    @Override
    public float getOpenPrice(Date date) {
        final List<KLine> kLines = mKLines;
        float capital = getCapitalInDate(date, kLines);
        for (KLine kLine : kLines) {
            if (kLine instanceof TradePair) {
                final float openP = kLine.getOpenPrice(date);
                if (openP != 0f) {
                    Log.d("TEST", "plus open:  " + kLine.getStock().getName() + " amount: " + openP);
                }
                capital += kLine.getOpenPrice(date);
            }
        }
        return capital;
    }

    @Override
    public float getClosePrice(Date date) {
        final List<KLine> kLines = mKLines;
        float capital = getCapitalInDate(date, kLines);
        for (KLine kLine : kLines) {
            if (kLine instanceof TradePair) {
                final float openP = kLine.getClosePrice(date);
                if (openP != 0f) {
                    Log.d("TEST", "plus closeP: " + kLine.getStock().getName() + " amount: " + openP);
                }
                capital += kLine.getClosePrice(date);
            }
        }
        return capital;
    }

    @Override
    public Date fromDate() {
        return Date.parseDateFromNumber(Constants.STARTING_DATE);
    }

    @Override
    public Date toDate() {
        return Date.parseDateFromCalendar(Calendar.getInstance());
    }


    @Override
    public Stock getStock() {
        return Stock.MY_FINACE;
    }

    @Override
    public void refreshPrices() {
        final List<KLine> kLines = mKLines;
        for (KLine kLine : kLines) {
            kLine.refreshPrices();
        }
    }

    private float getCapitalInDate(Date date, final List<KLine> kLines) {
        final int size = kLines.size();
        float capital = 0;
        for (int i = 0; i < size; i++) {
            final KLine kLine = kLines.get(i);
            if (kLine instanceof SingleTrade && kLine.fromDate().getDateNumber() <= date.getDateNumber()) {
                capital += kLine.getOpenPrice(kLine.fromDate());
            } else if (kLine instanceof TradePair && kLine.toDate().getDateNumber() < date.getDateNumber()) {
                capital += ((TradePair) kLine).getDletaPrice();
            }
        }
        return capital;
    }

    public List<KLine> getKLines() {
        return mKLines;
    }


}
