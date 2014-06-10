package com.danliu.stock.model;

import java.util.List;

public class CustomStock extends Stock {

    protected CustomStock(String id, String name) {
        super(id, name);
    }

    @Override
    public void refreshPrices() {
        // do not refresh the price of custom price.
    }

    @Override
    public void setHistoricalPrice(List<StockPrice> prices) {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public void updateCurrentPrice(StockPrice price) {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public float getClosePrice(Date date) {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public float getMaxPrice(Date date) {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public float getMinPrice(Date date) {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public float getOpenPrice(Date date) {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public String getPrefixForGoogle() {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public String getPrefixForSina() {
        throw new RuntimeException("do not support this operation.");
    }

    @Override
    public String getPrefixForYahoo() {
        throw new RuntimeException("do not support this operation.");
    }

}
