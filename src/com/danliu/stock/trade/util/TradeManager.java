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

import com.danliu.stock.model.TradeInfo;
import com.danliu.stock.util.AppContext;
import android.R.id;
import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

        public void onTradeInfoChanged(List<TradeInfo> newTradeInfos);

    }

    private static final String TRADE_FILE = "/sdcard/finance.txt";

    private static TradeManager sInstance;

    public static final TradeManager getInstance() {
        if (sInstance == null) {
            sInstance = new TradeManager(AppContext.getInstance());
        }
        return sInstance;
    }

    private Context mContext;
    private List<TradeInfo> mTradeInfos;
    private List<WeakReference<OnTradeInfosChangedListener>> mListeners;

    private TradeManager(final Context context) {
        mContext = context;
        loadTrades();
    }

    private void loadTrades() {
    }

    private void importTrades() {
        final List<TradeInfo> tradeInfos = new ArrayList<TradeInfo>();
        final File file = new File(TRADE_FILE);
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
                TradeInfo tradeInfo = TradeInfo.parseFileLine(line);
                if (tradeInfo != null) {
                    tradeInfos.add(tradeInfo);
                }
            }
            reader.close();
        } catch (IOException e) {
        }
        mTradeInfos = tradeInfos;
        save(tradeInfos);
    }

    private void notifyTradeInfosChanged(final List<TradeInfo> tradeInfos) {
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

    public List<TradeInfo> getTradeInfos() {
        return mTradeInfos;
    }

    public void appendTradeInfo(final TradeInfo tradeInfo) {
        mTradeInfos.add(tradeInfo);
        notifyTradeInfosChanged(mTradeInfos);
        save(mTradeInfos);
    }

    private void save(final List<TradeInfo> tradeInfos) {

    }

    public void addOnTradeInfosChangedListener(final OnTradeInfosChangedListener listener) {

    }

}
