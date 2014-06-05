/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    TradeInfo
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 30, 2014
 *    @version: 1.0
 *
 ******************************************************************************/

package com.danliu.stock.model;

import org.json.JSONException;
import org.json.JSONObject;
import android.database.Cursor;
import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TradeInfo of MyStock.
 *
 * @author danliu
 */
public class Trade {

    public enum TradeType {
        BUY(0), SELL(1), TRANSFER_IN(2), TRANSFER_OUT(3), TAX(4), NONE(5);

        private static final Pattern TYPE_PATERN = Pattern.compile("(.+)\\(.+\\)");
        private int mType;

        TradeType(final int type) {
            this.mType = type;
        }

        @Override
        public String toString() {
            if (mType == 0) {
                return "证券买入";
            } else if (mType == 1) {
                return "证券卖出";
            } else if (mType == 2) {
                return "银行转存";
            } else if (mType == 3) {
                return "银行转取";
            } else if (mType == 4) {
                return "税";
            } else {
                return "未知";
            }
        }

        public static TradeType get(final String fullTypeString) {
            final Matcher matcher = TYPE_PATERN.matcher(fullTypeString);
            if (matcher.find()) {
                final String type = matcher.group(1);
                if (TextUtils.equals(type, "证券买入")) {
                    return BUY;
                } else if (TextUtils.equals(type, "证券卖出")) {
                    return SELL;
                } else if (type.contains("税")) {
                    return TAX;
                } else {
                    return NONE;
                }
            } else {
                if (TextUtils.equals(fullTypeString, "银行转存")) {
                    return TRANSFER_IN;
                } else if (TextUtils.equals(fullTypeString, "银行转取")) {
                    return TRANSFER_OUT;
                } else {
                    return NONE;
                }
            }
        }

    }

    private static final String JSON_STOCK_NAME = "name";
    private static final String JSON_STOCK_ID = "id";
    private static final String JSON_DATE = "date";
    private static final String JSON_PRICE = "price";
    private static final String JSON_COUNT = "count";
    private static final String JSON_AMOUNT = "amount";
    private static final String JSON_BALANCE = "balance";
    private static final String JSON_TYPE = "type";

    private static final int INDEX_NAME = 1;
    private static final int INDEX_DATE = 2;
    private static final int INDEX_PRICE = 3;
    private static final int INDEX_COUNT = 4;
    private static final int INDEX_AMOUNT = 5;
    private static final int INDEX_BALANCE = 6;
    private static final int INDEX_TYPE = 8;
    private static final int INDEX_ID = 13;

    private Stock mStock;
    private Date mTradeDate;
    private float mTradePrice;
    private float mTradeCount;
    private float mTradeAmount;
    private float mBalance;
    private TradeType mTradeType;

    public Stock getStock() {
        return mStock;
    }
    public Date getTradeDate() {
        return mTradeDate;
    }

    public float getTradePrice() {
        return mTradePrice;
    }

    public float getTradeCount() {
        return mTradeCount;
    }

    public float getTradeAmount() {
        return mTradeAmount;
    }

    public float getBalance() {
        return mBalance;
    }

    public TradeType getTradeType() {
        return mTradeType;
    }

    public static final Trade parseFileLine(final String tradeString) {
        if (TextUtils.isEmpty(tradeString)) {
            return null;
        }
        final String[] splitResult = tradeString.split("\\s+");
        try {
            final Trade tradeInfo = new Trade();
            if (tradeString.indexOf(TradeType.TRANSFER_IN.toString()) > 0
                    || tradeString.indexOf(TradeType.TRANSFER_OUT.toString()) > 0) {
                tradeInfo.mStock = Stock.BANK_OPERATION;
                tradeInfo.mTradeDate = Date.parseDateFromNumber(Long.parseLong(splitResult[INDEX_DATE - 1]));
                tradeInfo.mTradeAmount = Float.parseFloat(splitResult[INDEX_AMOUNT - 1]);
                tradeInfo.mBalance = Float.parseFloat(splitResult[INDEX_BALANCE - 1]);
                tradeInfo.mTradeType = TradeType.get(splitResult[INDEX_TYPE - 1]);
            } else {
                tradeInfo.mStock = new Stock(splitResult[INDEX_ID], splitResult[INDEX_NAME]);
                tradeInfo.mTradeDate = Date.parseDateFromNumber(Long.parseLong(splitResult[INDEX_DATE]));
                tradeInfo.mTradeAmount = Float.parseFloat(splitResult[INDEX_AMOUNT]);
                tradeInfo.mBalance = Float.parseFloat(splitResult[INDEX_BALANCE]);
                tradeInfo.mTradeType = TradeType.get(splitResult[INDEX_TYPE]);
                try {
                    tradeInfo.mTradeCount = Float.parseFloat(splitResult[INDEX_COUNT]);
                    tradeInfo.mTradePrice = Float.parseFloat(splitResult[INDEX_PRICE]);
                } catch (Exception e) {
                }
            }
            return tradeInfo;
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mStock.toString());
        builder.append(" date: ").append(mTradeDate);
        builder.append(" price: ").append(mTradePrice);
        builder.append(" count: ").append(mTradeCount);
        builder.append(" amount: ").append(mTradeAmount);
        builder.append(" balance: ").append(mBalance);
        builder.append(" type: ").append(mTradeType);
        return builder.toString();
    }

    public Object toJsonObject() {
        final JSONObject result = new JSONObject();
        try {
            result.put(JSON_STOCK_ID, mStock.getId());
            result.put(JSON_STOCK_NAME, mStock.getName());
            result.put(JSON_PRICE, mTradePrice);
            result.put(JSON_DATE, mTradeDate);
            result.put(JSON_COUNT, mTradeCount);
            result.put(JSON_BALANCE, mBalance);
            result.put(JSON_AMOUNT, mTradeAmount);
            result.put(JSON_TYPE, mTradeType.toString());
        } catch (JSONException e) {
        }
        return result;
    }

    public static Trade parseJsonObject(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        }
        Trade result = new Trade();
        final String id = jsonObject.getString(JSON_STOCK_ID);
        final String name = jsonObject.getString(JSON_STOCK_NAME);
        result.mStock = new Stock(id, name);
        result.mBalance = Float.parseFloat(jsonObject.getString(JSON_BALANCE));
        result.mTradeAmount = Float.parseFloat(jsonObject.getString(JSON_AMOUNT));
        result.mTradeCount = Float.parseFloat(jsonObject.getString(JSON_COUNT));
        result.mTradeDate = Date.parseDateFromNumber(jsonObject.getLong(JSON_DATE));
        result.mTradePrice = Float.parseFloat(jsonObject.getString(JSON_PRICE));
        result.mTradeType = TradeType.get(jsonObject.getString(JSON_TYPE));
        return result;
    }
}
