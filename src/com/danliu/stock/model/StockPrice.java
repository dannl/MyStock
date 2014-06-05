/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    StockPrice
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Jun 3, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * StockPrice of MyStock.
 * @author danliu
 *
 */
public class StockPrice implements KLineItem {

    private static final String JSON_MAX = "max";
    private static final String JSON_MIN = "min";
    private static final String JSON_OPEN = "open";
    private static final String JSON_CLOSE = "close";
    private static final String JSON_DATE = "date";
    private static final String JSON_VOLUME = "volume";

    private float mMax;
    private float mMin;
    private float mOpen;
    private float mClose;
    private int mVolume;
    private Date mDate;

    private StockPrice() {}

    @Override
    public float getMaxPrice() {
        return mMax;
    }

    @Override
    public float getMinPrice() {
        return mMin;
    }

    @Override
    public float getOpenPrice() {
        return mOpen;
    }

    @Override
    public float getClosePrice() {
        return mClose;
    }

    public Date getDate() {
        return mDate;
    }

    public JSONObject toJsonObject() {
        try {
            final JSONObject result = new JSONObject();
            result.put(JSON_DATE, mDate.getDateNumber());
            result.put(JSON_OPEN, mOpen);
            result.put(JSON_MAX, mMax);
            result.put(JSON_MIN, mMin);
            result.put(JSON_CLOSE, mClose);
            result.put(JSON_VOLUME, mVolume);
            return result;
        } catch (Exception e) {
        }
        return null;
    }

    public static StockPrice parseJson(JSONObject obj) throws JSONException {
        final StockPrice result = new StockPrice();
        long date = obj.getLong(JSON_DATE);
        result.mDate = Date.parseDateFromNumber(date);
        result.mOpen = Float.parseFloat(obj.getString(JSON_OPEN));
        result.mMax = Float.parseFloat(obj.getString(JSON_MAX));
        result.mMin = Float.parseFloat(obj.getString(JSON_MIN));
        result.mClose = Float.parseFloat(obj.getString(JSON_CLOSE));
        result.mVolume = obj.getInt(JSON_VOLUME);
        return result;
    }

    public static StockPrice parseStringLine(String line) throws Exception {
        final String[] splited = line.split(",");
        StockPrice result = new StockPrice();
        result.mDate = Date.parseDateFromYahooStr(splited[0]);
        result.mOpen = Float.parseFloat(splited[1]);
        result.mMax = Float.parseFloat(splited[2]);
        result.mMin = Float.parseFloat(splited[3]);
        result.mClose = Float.parseFloat(splited[4]);
        result.mVolume = Integer.parseInt(splited[5]);
        return result;
    }

    public static StockPrice parseSinaRequestResult(String resultStr) throws Exception {
        final int startIndex = resultStr.indexOf("\"");
        final int endIndex = resultStr.lastIndexOf("\"");
        String src = resultStr.subSequence(startIndex, endIndex).toString();
        String[] splited = src.split(",");
        StockPrice result = new StockPrice();
        result.mDate = Date.parseDateFromCalendar(Calendar.getInstance());
        result.mOpen = Float.parseFloat(splited[1]);
        result.mMax = Float.parseFloat(splited[4]);
        result.mMin = Float.parseFloat(splited[5]);
        result.mClose = Float.parseFloat(splited[3]);
        result.mVolume = Integer.parseInt(splited[8]);
        return result;
    }

}
