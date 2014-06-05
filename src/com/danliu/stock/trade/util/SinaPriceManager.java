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

import com.danliu.stock.model.Stock;
import com.danliu.stock.model.StockPrice;
import com.danliu.stock.util.AppContext;
import com.danliu.stock.util.Constants;
import com.dolphin.browser.Network.HttpRequester;
import com.dolphin.browser.Network.HttpUtils;
import com.dolphin.browser.Network.HttpUtils.HttpRequestResult;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import android.content.Context;
import java.io.IOException;

/**
 * SinaPriceManager of MyStock.
 *
 * @author danliu
 */
public class SinaPriceManager {

    private static SinaPriceManager sInstance;
    private static final String DESKTOP_USERAGENT_FORMAT = "Mozilla/5.0 (Macintosh; "
            + "U; Intel Mac OS X 10_6_3; en-us) AppleWebKit/10 (KHTML, "
            + "like Gecko) Version/5.0 Safari/10";

    public static final SinaPriceManager getInstance() {
        if (sInstance == null) {
            sInstance = new SinaPriceManager();
        }
        return sInstance;
    }


    private SinaPriceManager() {
    }

    public StockPrice requestCurrentPrice(final Stock stock) {
        final String url = String.format(Constants.CURRENT_PRICE_URL, stock.getPrefixForSina(),
                stock.getId());
        HttpRequester.Builder builder = new HttpRequester.Builder(url);
        builder.UserAgent(DESKTOP_USERAGENT_FORMAT);
        builder.KeepAlive(true);
        try {
            final HttpRequestResult result = builder.build().request();
            if (result.status.getStatusCode() == HttpStatus.SC_OK) {
                final StockPrice price = StockPrice.parseSinaRequestResult(HttpUtils.decodeEntityAsString(result.entity));
                return price;
            }
        } catch (IOException e) {
        }
        return null;

    }

}
