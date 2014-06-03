
package com.danliu.stock;

import com.danliu.stock.model.Trade;
import com.danliu.stock.trade.util.TradeManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<Trade> tradeInfos = TradeManager.getInstance().getTradeInfos();
        for (Trade tradeInfo : tradeInfos) {
            Log.d("TEST", tradeInfo.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
