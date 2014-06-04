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
        return 0;
    }

    @Override
    public float getMinPrice(Date date) {
        return 0;
    }

    @Override
    public float getOpenPrice(Date date) {
        return 0;
    }

    @Override
    public float getClosePrice(Date date) {
        return 0;
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
        return Stock.MY_FINACE;
    }

}
