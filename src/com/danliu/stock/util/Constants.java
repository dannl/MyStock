/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    Constants
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 29, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.util;

import java.util.HashMap;

/**
 * Constants of MyStock.
 * @author danliu
 *
 */
public class Constants {

    public static final long STARTING_DATE = 20140401;

    public static final String SH_FOR_GOOGLE = "SHA";

    public static final String SZ_FOR_GOOGLE = "SZA";

    public static final String SH_FOR_YAHOO = "SS";

    public static final String SZ_FOR_YAHOO = "SZ";

    public static final String SZ_FOR_SINA = "sz";

    public static final String SH_FOR_SINA = "sh";

    public static final String HISTORY_DOWNLOAD_URL = "http://ichart.finance.yahoo.com/table.csv?s=%s.%s&a=%s&b=%s&c=%s&d=%s&e=%s&f=%s&g=d&ignore=.csv";

//    public static final String HISTORY_REQUEST_URL = "http://www.google.com/finance/historical?q=%s:%s&start=%s&num=%s";
    public static final String HISTORY_REQUEST_URL = "http://finance.yahoo.com/q/hp?s=600000.SS&a=10&b=10&c=1999&d=05&e=4&f=2014&g=d";

    public static final String CURRENT_PRICE_URL = "http://hq.sinajs.cn/list=%s%s";

    public static final float STAMP_TAX = 0.001f;

    public static final float BROKERAGE_RATE = 0.009f;

    public static final float MIN_BROKERAGE = 5.0f;

    public static final float OWNERSHIP_TRANSFER_RATE = 0.0003f;
}
