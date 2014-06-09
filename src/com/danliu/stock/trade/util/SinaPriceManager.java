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

import com.danliu.stock.model.Date;
import com.danliu.stock.model.Stock;
import com.danliu.stock.model.StockPrice;
import com.danliu.stock.util.AppContext;
import com.danliu.stock.util.Constants;
import com.dolphin.browser.Network.HttpRequester;
import com.dolphin.browser.Network.HttpUtils;
import com.dolphin.browser.Network.HttpUtils.HttpRequestResult;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

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
    private static final String PREF_NAME = "sina";
    private HashMap<String, StockPrice> PRICE_CACHE = new HashMap<String, StockPrice>();
    private static final long REFRESH_INTERVAL = 120 * 1000;

    public static final SinaPriceManager getInstance() {
        if (sInstance == null) {
            sInstance = new SinaPriceManager(AppContext.getInstance());
        }
        return sInstance;
    }

    private SharedPreferences mPreferences;
    private SinaPriceManager(final Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public StockPrice getPrice(final Stock stock) {
        String stockId = stock.getId();
        final long lastUpdate = mPreferences.getLong(stockId, 0);
        final long current = System.currentTimeMillis();
        if (PRICE_CACHE.containsKey(stockId) && current - lastUpdate < REFRESH_INTERVAL) {
            return PRICE_CACHE.get(stockId);
        }
        final String url = String.format(Constants.CURRENT_PRICE_URL, stock.getPrefixForSina(),
                stockId);
        HttpRequester.Builder builder = new HttpRequester.Builder(url);
        builder.UserAgent(DESKTOP_USERAGENT_FORMAT);
        builder.KeepAlive(true);
        try {
            final HttpRequestResult result = builder.build().request();
            if (result.status.getStatusCode() == HttpStatus.SC_OK) {
                final StockPrice price = StockPrice.parseSinaRequestResult(HttpUtils.decodeEntityAsString(result.entity));
                mPreferences.edit().putLong(stockId, current).apply();
                PRICE_CACHE.put(stockId, price);
                return price;
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return null;

    }

}
