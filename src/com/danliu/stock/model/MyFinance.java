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

import com.danliu.stock.util.Constants;
import java.util.Calendar;
import java.util.List;

/**
 * MyFinance of MyStock.
 * @author danliu
 *
 */
public class MyFinance implements KLine {

    private List<KLine> mKLines;

    public MyFinance(final List<KLine> kLines) {
        mKLines = kLines;
    }

    @Override
    public float getMaxPrice(Date date) {
        final List<KLine> kLines = mKLines;
        float capital = getCapitalInDate(date, kLines);
        for (KLine kLine : kLines) {
            if (kLine instanceof TradePair) {
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
            if (kLine instanceof SingleTrade && kLine.fromDate().getDateNumber() < date.getDateNumber()) {
                capital += kLine.getOpenPrice(kLine.fromDate());
            }
        }
        return capital;
    }


}
