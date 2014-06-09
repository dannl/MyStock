
package com.danliu.stock;

import com.danliu.stock.model.Date;
import com.danliu.stock.model.KLine;
import com.danliu.stock.model.MyFinance;
import com.danliu.stock.model.Stock;
import com.danliu.stock.model.StockPrice;
import com.danliu.stock.model.Trade;
import com.danliu.stock.trade.util.SinaPriceManager;
import com.danliu.stock.trade.util.YahooPriceManager;
import com.danliu.stock.trade.util.TradeManager;
import com.danliu.stock.util.Constants;
import com.dolphin.browser.util.IOUtilities;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<Trade> tradeInfos = TradeManager.getInstance().getAllTrades();
//        for (Trade tradeInfo : tradeInfos) {
//            for (int i = 0; i < 10000; i++) {
//                if (i == 1) {
//                    break;
//                }
//            }
//            Log.d("TEST", tradeInfo.toString());
//        }
        test();
//        final File file = new File(getCacheDir(), "google");
//        final File r = new File(file, "temp.txt");
//        String content = IOUtilities.readFileText(r);
//        content = content.replaceAll("\n", "");
//        try {
//            IOUtilities.saveToFile(r, content, "UTF-8");
//        } catch (IOException e) {
//        }
    }

    private void test() {
        final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                final Stock stock = new Stock("600000", "浦发银行");
//                YahooPriceManager.getInstance().getPrices(stock);
//                StockPrice price = SinaPriceManager.getInstance().getPrice(stock);
                MyFinance finance = new MyFinance();
                finance.loadKLines();
                finance.refreshPrices();
                Date fromDate = Date.parseDateFromNumber(Constants.STARTING_DATE);
                Date toDate = Date.parseDateFromCalendar(Calendar.getInstance());
                for (long i = fromDate.getDateNumber(); i <= toDate.getDateNumber(); i++) {
                    if (!Date.isValidateDate(i)) {
                        continue;
                    }
                    Date date = Date.parseDateFromNumber(i);
                    Log.d("TEST", "date: " + date.toString() + " OPEN: " + finance.getOpenPrice(date) + " MAX:" + finance.getMaxPrice(date) + " MIN: " + finance.getMinPrice(date) + " CLOSE: " + finance.getClosePrice(date));
                }
                return null;
            }};
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
