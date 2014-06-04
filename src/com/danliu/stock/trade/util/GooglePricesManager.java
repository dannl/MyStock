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
import com.danliu.stock.util.HttpHelper;
import com.danliu.util.Log;
import com.dolphin.browser.Network.HttpRequester;
import com.dolphin.browser.Network.HttpUtils;
import com.dolphin.browser.Network.HttpUtils.HttpRequestResult;
import com.dolphin.browser.util.IOUtilities;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


/**
 * PriceManager of MyStock.
 * @author danliu
 *
 */
public class GooglePricesManager {

    private static final String DESKTOP_USERAGENT_FORMAT = "Mozilla/5.0 (Macintosh; "
            + "U; Intel Mac OS X 10_6_3; en-us) AppleWebKit/10 (KHTML, "
            + "like Gecko) Version/5.0 Safari/10";
    private static GooglePricesManager sInstance;
    private static final String CACHE_DIR = "google";
    private static final String PREF_FILE_NAME = "google";
    private static final String PREF_LAST_UPDATE = "last_update";
    private static final HashMap<String, List<StockPrice>> STOCK_PRICES = new HashMap<String, List<StockPrice>>();
    private static final int MAX_PRICE_COUNT = 200;

    public static final GooglePricesManager getInstance() {
        if (sInstance == null) {
            sInstance = new GooglePricesManager(AppContext.getInstance());
        }
        return sInstance;
    }

    private Context mContext;
    private SharedPreferences mPreferences;

    private GooglePricesManager(final Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public List<StockPrice> getPrices(final Stock stock) {
        final String stockId = stock.getId();
        final Calendar calendar = Calendar.getInstance();
        final Date date = Date.parseDateFromCalendar(calendar);
        final long lastUpdate = mPreferences.getLong(PREF_LAST_UPDATE, 0);
        if (date.getDate() > lastUpdate) {
            //we need to update it.
            return requestStockPrices(stock);
        } else if (!STOCK_PRICES.containsKey(stockId)) {
            return parseStockFromFile(stock);
        } else {
            return STOCK_PRICES.get(stockId);
        }

    }

    private List<StockPrice> requestStockPrices(final Stock stock) {
        final Date currentDate = Date.parseDateFromCalendar(Calendar.getInstance());
        int count = (int) (currentDate.getDate() - Math.min(Constants.STARTING_DATE, mPreferences.getLong(PREF_LAST_UPDATE, Constants.STARTING_DATE)));
        final List<StockPrice> result = new ArrayList<StockPrice>();
        int startingIndex = 0;
        while (count > 0) {
//            final String url = String.format(Constants.HISTORY_REQUEST_URL, stock.getPrefixForGoogle(), stock.getId(), startingIndex, Math.min(count, MAX_PRICE_COUNT));
            final String url = "http://finance.yahoo.com/q/hp?s=600000.SS&a=10&b=10&c=1999&d=05&e=4&f=2014&g=d";
            HttpRequester.Builder builder = new HttpRequester.Builder(url);
            builder.ConnectTimeout(20000);
            builder.UserAgent(DESKTOP_USERAGENT_FORMAT);
            try {
                HttpRequestResult requestResult = builder.build().request();
                if (requestResult.status.getStatusCode() == HttpStatus.SC_OK) {
                    String content = HttpUtils.decodeEntityAsString(requestResult.entity);
                    content = content.replaceAll("\n","");
                    final File toFile = new File(getCacheDir(), "temp.txt");
                    IOUtilities.saveToFile(toFile, content, "UTF-8");
                    Pattern pattern = Pattern.compile(content);
                }
            } catch (IOException e) {
                Log.w(e);
            }
            count -= MAX_PRICE_COUNT;
            startingIndex += MAX_PRICE_COUNT;
        }
        return null;
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
                final StockPrice priceItem = StockPrice.parseJson(stock, obj);
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
