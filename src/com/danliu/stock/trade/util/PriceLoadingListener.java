/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    OnPriceLoadedListener
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 4, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.trade.util;

import com.danliu.stock.model.StockPrice;
import java.util.List;

/**
 * OnPriceLoadedListener of MyStock.
 * @author danliu
 *
 */
public interface PriceLoadingListener {

    public void onPriceLoaded(final List<StockPrice> prices);

}
