
package com.danliu.stock;

import com.danliu.stock.model.Stock;
import com.danliu.stock.model.Trade;
import com.danliu.stock.trade.util.GooglePricesManager;
import com.danliu.stock.trade.util.TradeManager;
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
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final List<Trade> tradeInfos = TradeManager.getInstance().getTradeInfos();
//        for (Trade tradeInfo : tradeInfos) {
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
                GooglePricesManager.getInstance().getPrices(stock);
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
