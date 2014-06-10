
package com.danliu.stock;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.danliu.stock.model.KLine;
import com.danliu.stock.model.MyFinance;
import com.danliu.stock.view.KLineView;

public class MainActivity extends Activity {

    private KLine mKLine;
    private KLineView mKLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKLineView = new KLineView(this);
        setContentView(mKLineView);
        mKLineView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mKLineView.invalidate();
            }
        });
        loadKline();
    }

    private void loadKline() {
        final AsyncTask<Void, Void, KLine> task = new AsyncTask<Void, Void, KLine>() {

            @Override
            protected KLine doInBackground(Void... params) {
                MyFinance finance = new MyFinance();
                finance.loadTrades();
                finance.refreshPrices();
//                Date fromDate = Date.parseDateFromNumber(Constants.STARTING_DATE);
//                Date toDate = Date.parseDateFromCalendar(Calendar.getInstance());
//                for (long i = fromDate.getDateNumber(); i <= toDate.getDateNumber(); i++) {
//                    if (!Date.isValidateDate(i)) {
//                        continue;
//                    }
//                    if (i == 20140610) {
//                        int fu = 1;
//                    }
//                    Date date = Date.parseDateFromNumber(i);
//                    Log.d("TEST", "date: " + date.toString() + " OPEN: " + finance.getOpenPrice(date) + " MAX:" + finance.getMaxPrice(date) + " MIN: " + finance.getMinPrice(date) + " CLOSE: " + finance.getClosePrice(date));
//                }
                return finance;
            }

            @Override
            protected void onPostExecute(KLine result) {
                if (result != null) {
                    mKLineView.setKLine(result);
                }
            }
        };

        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
