/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    KLineItem
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 5, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

/**
 * KLineItem of MyStock.
 * @author danliu
 *
 */
public interface KLineItem {

    public float getMaxPrice();
    public float getMinPrice();
    public float getOpenPrice();
    public float getClosePrice();

}
