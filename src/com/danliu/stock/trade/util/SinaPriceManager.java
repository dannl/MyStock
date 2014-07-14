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
    private static final String KEY_CACHED_PRICE_TAIL = "_last_cached";
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
        StockPrice price = requestPriceFromServer(stock, stockId, current);
        if (price == null) {
            price = loadCachedPrice(stockId, lastUpdate, current);
        }
        if (price != null) {
            PRICE_CACHE.put(stockId, price);
        }
        return price;
    }

    private StockPrice loadCachedPrice(final String stockId, final long lastUpdate,
            final long current) {
        if (lastUpdate != 0) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(current);
            int currentYear = c.get(Calendar.YEAR);
            int currentMonth = c.get(Calendar.MONTH);
            int currentDay = c.get(Calendar.DAY_OF_MONTH);
            c.setTimeInMillis(lastUpdate);
            if (currentYear == c.get(Calendar.YEAR) && currentMonth == c.get(Calendar.MONTH)
                    && currentDay == c.get(Calendar.DAY_OF_MONTH)) {
                final String cachedPrice = mPreferences.getString(stockId + KEY_CACHED_PRICE_TAIL,
                        "");
                try {
                    StockPrice price = StockPrice.parseSinaRequestResult(cachedPrice);
                    return price;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private StockPrice requestPriceFromServer(final Stock stock, String stockId, final long current) {
        final String url = String.format(Constants.CURRENT_PRICE_URL, stock.getPrefixForSina(),
                stockId);
        HttpRequester.Builder builder = new HttpRequester.Builder(url);
        builder.UserAgent(DESKTOP_USERAGENT_FORMAT);
        builder.KeepAlive(true);
        try {
            final HttpRequestResult result = builder.build().request();
            if (result.status.getStatusCode() == HttpStatus.SC_OK) {
                String serverResultStr = HttpUtils.decodeEntityAsString(result.entity);
                final StockPrice price = StockPrice.parseSinaRequestResult(serverResultStr);
                mPreferences.edit().putLong(stockId, current).apply();
                return price;
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return null;
    }

}
