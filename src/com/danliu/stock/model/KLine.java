/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    KLine
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

/**
 * KLine of MyStock.
 * @author danliu
 *
 */
public interface KLine {

    public Stock getStock();
    public float getMaxPrice(final Date date);
    public float getMinPrice(final Date date);
    public float getOpenPrice(final Date date);
    public float getClosePrice(final Date date);
    public Date fromDate();
    public Date toDate();

}
