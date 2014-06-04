/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    SinaPriceManager
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 4, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.trade.util;


/**
 * SinaPriceManager of MyStock.
 * @author danliu
 *
 */
public class SinaPriceManager {

    private static SinaPriceManager sInstance;

    public static final SinaPriceManager getInstance() {
        if (sInstance == null) {
            sInstance = new SinaPriceManager();
        }
        return sInstance;
    }

}
