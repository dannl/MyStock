/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    PriceManager
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/

package com.danliu.stock.trade.util;

import com.danliu.stock.model.Date;
import com.danliu.stock.model.Stock;
import com.danliu.stock.model.StockPrice;
import com.danliu.stock.util.AppContext;
import com.danliu.stock.util.Constants;
import com.danliu.util.Log;
import com.dolphin.browser.Network.HttpUtils;
import com.dolphin.browser.util.IOUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * PriceManager of MyStock.
 *
 * @author danliu
 */
public class YahooPriceManager {

    private static YahooPriceManager sInstance;
    private static final String CACHE_DIR = "yahoo";
    private static final String PREF_FILE_NAME = "yahoo";
    private static final HashMap<String, List<StockPrice>> STOCK_PRICES = new HashMap<String, List<StockPrice>>();

    public static final YahooPriceManager getInstance() {
        if (sInstance == null) {
            sInstance = new YahooPriceManager(AppContext.getInstance());
        }
        return sInstance;
    }

    private Context mContext;
    private SharedPreferences mPreferences;

    private YahooPriceManager(final Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public List<StockPrice> getPrices(final Stock stock) {
        final String stockId = stock.getId();
        final Calendar calendar = Calendar.getInstance();
        final Date date = Date.parseDateFromCalendar(calendar);
        final long lastUpdate = mPreferences.getLong(stockId, 0);
        if (date.getDateNumber() > lastUpdate) {
            // we need to update it.
            return requestStockPrices(stock);
        } else if (!STOCK_PRICES.containsKey(stockId)) {
            return parseStockFromFile(stock);
        } else {
            return STOCK_PRICES.get(stockId);
        }

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("resource")
    private List<StockPrice> requestStockPrices(final Stock stock) {
        final Date currentDate = Date.parseDateFromCalendar(Calendar.getInstance());
        currentDate.yesterday();
        final long lastUpdate = mPreferences.getLong(stock.getId(), Constants.STARTING_DATE);
        final Date fromDate = Date.parseDateFromNumber(lastUpdate);
        // http://ichart.finance.yahoo.com/table.csv?s=600000.SS&a=10&b=10&c=1999&d=05&e=4&f=2014&g=d&ignore=.csv
        final String downloadUrl = String.format(Constants.HISTORY_DOWNLOAD_URL, stock.getId(),
                stock.getPrefixForYahoo(), fromDate.month() - 1, fromDate.day(), fromDate.year(),
                currentDate.month(), currentDate.day(), currentDate.year());
        // Log.d("TEST", downloadUrl);
        final File tempFile = new File(getCacheDir(), "temp");
        HttpUtils.downloadFile(downloadUrl, tempFile, 1024 * 1024 * 10, false);
        try {
            final List<StockPrice> prices = dealWithRequestResult(stock, tempFile);
            mPreferences.edit().putLong(stock.getId(), currentDate.getDateNumber()).apply();
            ;
            return prices;
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("resource")
    private List<StockPrice> dealWithRequestResult(final Stock stock, final File tempFile) throws IOException, JSONException {
        final List<StockPrice> prices = new ArrayList<StockPrice>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                new FileInputStream(tempFile))));
        String line = null;
        while ((line = reader.readLine()) != null) {
            // Log.d("TEST", line);
            try {
                final StockPrice price = StockPrice.parseStringLine(line);
                prices.add(price);
            } catch (Exception e) {
            }
        }
        boolean flushCache = !prices.isEmpty();
        final File cachedFile = new File(getCacheDir(), stock.getId());
        if (cachedFile.exists()) {
            FileInputStream in = new FileInputStream(cachedFile);
            final byte[] buffer = new byte[in.available()];
            JSONArray array = new JSONArray(new String(buffer));
            int length = array.length();
            for (int i = 0; i < length; i++) {
                final StockPrice price = StockPrice.parseJson(array.getJSONObject(i));
                prices.add(price);
            }
        }
        if (flushCache) {
            JSONArray arrayToSave = new JSONArray();
            for (int i = 0; i < prices.size(); i++) {
                arrayToSave.put(prices.get(i).toJsonObject());
            }
            IOUtilities.saveToFile(cachedFile, arrayToSave.toString(), "UTF-8");
        }
        return prices;
    }

    private List<StockPrice> parseStockFromFile(final Stock stock) {
        String stockId = stock.getId();
        try {
            FileInputStream in = new FileInputStream(new File(getCacheDir(), stockId));
            final byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            final JSONArray pricesArray = new JSONArray(new String(buffer));
            final int length = pricesArray.length();
            final List<StockPrice> prices = new ArrayList<StockPrice>(length);
            for (int i = 0; i < length; i++) {
                final JSONObject obj = pricesArray.getJSONObject(i);
                final StockPrice priceItem = StockPrice.parseJson(obj);
                prices.add(priceItem);
            }
            STOCK_PRICES.put(stockId, prices);
            return prices;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (JSONException e) {
        }
        return null;
    }

    private final File getCacheDir() {
        final File result = new File(mContext.getCacheDir(), CACHE_DIR);
        if (!result.exists()) {
            result.mkdirs();
        }
        return result;
    }
}
