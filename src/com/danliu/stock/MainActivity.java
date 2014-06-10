package com.danliu.stock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.danliu.stock.model.Date;
import com.danliu.stock.model.KLine;
import com.danliu.stock.model.MyFinance;
import com.danliu.stock.util.Constants;
import com.danliu.stock.view.KLineView;
import com.danliu.util.Log;

public class MainActivity extends Activity {

    private KLine mKLine;
    private KLineView mKLineView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKLineView = new KLineView(this);
        // setContentView(mKLineView);
        // mKLineView.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // mKLineView.invalidate();
        // }
        // });
        mListView = new ListView(this);
        setContentView(mListView);
        loadKline();
    }

    private void loadKline() {
        final AsyncTask<Void, Void, KLine> task = new AsyncTask<Void, Void, KLine>() {

            @Override
            protected KLine doInBackground(Void... params) {
                MyFinance finance = new MyFinance();
                finance.loadTrades();
                finance.refreshPrices();
                // Date fromDate =
                // Date.parseDateFromNumber(Constants.STARTING_DATE);
                // Date toDate =
                // Date.parseDateFromCalendar(Calendar.getInstance());
                // for (long i = fromDate.getDateNumber(); i <=
                // toDate.getDateNumber(); i++) {
                // if (!Date.isValidateDate(i)) {
                // continue;
                // }
                // if (i == 20140610) {
                // int fu = 1;
                // }
                // Date date = Date.parseDateFromNumber(i);
                // Log.d("TEST", "date: " + date.toString() + " OPEN: " +
                // finance.getOpenPrice(date) + " MAX:" +
                // finance.getMaxPrice(date) + " MIN: " +
                // finance.getMinPrice(date) + " CLOSE: " +
                // finance.getClosePrice(date));
                // }
                return finance;
            }

            @Override
            protected void onPostExecute(KLine result) {
                if (result != null) {
                    // mKLineView.setKLine(result);
                    mListView.setAdapter(new FinaceAdapter(result));
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

    private class FinaceAdapter extends BaseAdapter {

        private KLine mKLine;
        private List<Date> mDates;

        private FinaceAdapter(final KLine kLine) {
            mKLine = kLine;
            mDates = new ArrayList<Date>();
            Date fromDate = Date.parseDateFromNumber(Constants.STARTING_DATE);
            Date toDate = Date.parseDateFromCalendar(Calendar.getInstance());
            for (long i = fromDate.getDateNumber(); i <= toDate.getDateNumber(); i++) {
                if (!Date.isValidateDate(i)) {
                    continue;
                }
                Date date = Date.parseDateFromNumber(i);
                mDates.add(date);
            }
        }

        @Override
        public int getCount() {
            return mDates.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else {
                return mDates.get(position - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ContentView(MainActivity.this);
            }
            if (position == 0) {
                ((ContentView) convertView).bind(null, 0, 0, 0, 0);
            } else {
                final Date date = (Date) getItem(position);
                ((ContentView) convertView).bind(date,
                        mKLine.getOpenPrice(date), mKLine.getMaxPrice(date),
                        mKLine.getMinPrice(date), mKLine.getClosePrice(date));
            }
            return convertView;
        }

        private class ContentView extends LinearLayout {

            public ContentView(Context context) {
                super(context);
                for (int i = 0; i < 5; i++) {
                    final TextView text = new TextView(context);
                    final LayoutParams params = new LayoutParams(0,
                            LayoutParams.WRAP_CONTENT);
                    params.weight = 1;
                    addView(text, params);
                }
            }

            public void bind(final Date date, final float open,
                    final float max, final float min, final float close) {
                if (date == null) {
                    setTextColor(Color.BLACK);
                    ((TextView) getChildAt(0)).setText("日期");
                    ((TextView) getChildAt(1)).setText("开盘价");
                    ((TextView) getChildAt(2)).setText("最大");
                    ((TextView) getChildAt(3)).setText("最小");
                    ((TextView) getChildAt(4)).setText("收盘价");
                } else {
                    if (open > close) {
                        setTextColor(0xff00882a);
                    } else {
                        setTextColor(Color.RED);
                    }
                    ((TextView) getChildAt(0)).setText(date.toString());
                    ((TextView) getChildAt(1)).setText(String.valueOf(open));
                    ((TextView) getChildAt(2)).setText(String.valueOf(max));
                    ((TextView) getChildAt(3)).setText(String.valueOf(min));
                    ((TextView) getChildAt(4)).setText(String.valueOf(close));
                }
            }

            private void setTextColor(final int color) {
                final int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ((TextView) getChildAt(i)).setTextColor(color);
                }
            }

        }

    }

}
