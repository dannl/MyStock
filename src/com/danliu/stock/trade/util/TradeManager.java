/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    TradeManager
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 30, 2014
 *    @version: 1.0
 *
 ******************************************************************************/

package com.danliu.stock.trade.util;

import com.danliu.stock.model.KLine;
import com.danliu.stock.model.Trade;
import com.danliu.stock.util.AppContext;
import org.json.JSONArray;
import org.json.JSONException;
import android.R.id;
import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * TradeManager of MyStock.
 *
 * @author danliu
 */
public class TradeManager {

    public interface OnTradeInfosChangedListener {

        public void onTradeInfoChanged(List<Trade> newTradeInfos);

    }

    private static final String TRADE_FILE_IN_DATA = "trade";

    private static final String TRADE_FILE_IN_SDCARD = "/sdcard/finance.txt";

    private static TradeManager sInstance;

    public static final TradeManager getInstance() {
        if (sInstance == null) {
            sInstance = new TradeManager(AppContext.getInstance());
        }
        return sInstance;
    }

    private Context mContext;
    private List<Trade> mTradeInfos;
    private List<WeakReference<OnTradeInfosChangedListener>> mListeners;

    private TradeManager(final Context context) {
        mContext = context;
        loadTrades();
    }

    private void loadTrades() {
        final File tradeInfoFile = getTradesFileInData();
//        if (tradeInfoFile.exists()) {
//            loadTradesFromData(tradeInfoFile);
//        } else {
            loadTradesFromAssets();
//        }
    }

    private File getTradesFileInData() {
        final File cacheDir = mContext.getCacheDir();
        final File tradeInfoFile = new File(mContext.getCacheDir(), TRADE_FILE_IN_DATA);
        return tradeInfoFile;
    }

    private void loadTradesFromData(final File file) {
        final List<Trade> tradeInfos = new ArrayList<Trade>();
        try {
            InputStream in = new FileInputStream(file);
            final byte[] buffer = new byte[in.available()];
            in.read(buffer);
            JSONArray jsonArray = new JSONArray(new String(buffer));
            final int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    Trade tradeInfo = Trade.parseJsonObject(jsonArray.getJSONObject(i));
                    tradeInfos.add(tradeInfo);
                } catch (JSONException e) {
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (JSONException e) {
        }
        mTradeInfos = tradeInfos;
    }

    private void loadTradesFromAssets() {
        final List<Trade> tradeInfos = new ArrayList<Trade>();
        final File file = new File(TRADE_FILE_IN_SDCARD);
        InputStream in = null;
        try {
            if (file.exists()) {
                in = new FileInputStream(file);
            } else {
                in = mContext.getAssets().open("finance.txt");
            }
        } catch (IOException e) {
        }
        if (in == null) {
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                Trade tradeInfo = Trade.parseFileLine(line);
                if (tradeInfo != null) {
                    tradeInfos.add(tradeInfo);
                }
            }
            reader.close();
        } catch (IOException e) {
        }
        save(tradeInfos);
        mTradeInfos = tradeInfos;
    }

    private void notifyTradeInfosChanged(final List<Trade> tradeInfos) {
        final List<WeakReference<OnTradeInfosChangedListener>> listeners = mListeners;
        if (listeners == null) {
            return;
        }
        for (WeakReference<OnTradeInfosChangedListener> weakReference : listeners) {
            if (weakReference == null) {
                continue;
            }
            OnTradeInfosChangedListener onTradeInfosChangedListener = weakReference.get();
            if (onTradeInfosChangedListener == null) {
                continue;
            }
            onTradeInfosChangedListener.onTradeInfoChanged(tradeInfos);
        }
    }

    public List<Trade> getAllTrades() {
        return mTradeInfos;
    }

    public void appendTradeInfo(final Trade tradeInfo) {
        mTradeInfos.add(tradeInfo);
        notifyTradeInfosChanged(mTradeInfos);
        save(mTradeInfos);
    }

    private void save(final List<Trade> tradeInfos) {
        final JSONArray arrayToSave = new JSONArray();
        for (Trade tradeInfo : tradeInfos) {
            arrayToSave.put(tradeInfo.toJsonObject());
        }
        final File tradesFileInData = getTradesFileInData();
        FileOutputStream out;
        try {
            out = new FileOutputStream(tradesFileInData);
            out.write(arrayToSave.toString().getBytes());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public void addOnTradeInfosChangedListener(final OnTradeInfosChangedListener listener) {

    }
}
