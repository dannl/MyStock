package com.danliu.stock.view;

import java.util.Calendar;
import java.util.List;

import com.danliu.stock.model.Date;
import com.danliu.stock.model.KLine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class KLineView extends View {

    private static final float MAX_KLINE_ITEM_WIDTH = 80;

    private static final float MIN_KLINE_ITEM_WIDTH = 5;

    private static final float STARTING_KLINE_ITEM_WIDTH = 20;

    private Paint mGreenPaint;
    private Paint mReadPaint;
    private KLine mKLine;
    private float mKLineItemWidth;
    private Date mStartingDate;
    private float mMinPrice;
    private float mMaxPrice;

    public KLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public KLineView(Context context) {
        super(context);
        setup();
    }

    public KLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        mReadPaint = createPaint(Color.RED);
        mGreenPaint = createPaint(Color.GREEN);
        setBackgroundColor(Color.BLACK);
        mKLineItemWidth = STARTING_KLINE_ITEM_WIDTH;
    }

    private Paint createPaint(final int color) {
        final Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setStyle(Style.FILL);
        paint.setColor(color);
        return paint;
    }

    public void setKLine(final KLine kLine) {
        mKLine = kLine;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mKLine == null) {
            return;
        }
        final KLine kLine = mKLine;
        if (mStartingDate == null) {
            mStartingDate = kLine.toDate();
        }
        Date currentDate = mStartingDate;
        int maxKlineItemCount = (int) (getWidth() / mKLineItemWidth);
        float klineItemWidth = mKLineItemWidth;
        float viewHeight = getHeight();
        Date endDate = currentDate.daysBefore(maxKlineItemCount);
        if (endDate.getDateNumber() < kLine.fromDate().getDateNumber()) {
            endDate = kLine.fromDate();
        }

        float maxTotal = 0;
        float minTotal = Float.MAX_VALUE;
        for (int i = 0; i < maxKlineItemCount && currentDate.getDateNumber() >= endDate.getDateNumber(); i++) {
//            float open = kLine.getOpenPrice(currentDate);
//            float close = kLine.getClosePrice(currentDate);
            float max = kLine.getMaxPrice(currentDate);
            float min = kLine.getMinPrice(currentDate);
            if (max > maxTotal) {
                maxTotal = max;
            }
            if (min < minTotal && min != 0) {
                minTotal = min;
            }
            currentDate = currentDate.yesterday();
        }
        currentDate = mStartingDate;
        float deltaY = maxTotal - minTotal;
        float startX = getWidth();
        for (int i = 0; i < maxKlineItemCount && currentDate.getDateNumber() >= endDate.getDateNumber(); i++) {
            float open = kLine.getOpenPrice(currentDate) - minTotal;
            float close = kLine.getClosePrice(currentDate) - minTotal;
            float max = kLine.getMaxPrice(currentDate) - minTotal;
            float min = kLine.getMinPrice(currentDate) - minTotal;

            Paint paint;
            if (open > close) {
                paint = mGreenPaint;
            } else {
                paint = mReadPaint;
            }

            float solidStartX = startX - klineItemWidth;
            float solidEndX = startX;
            float solidStartY = viewHeight - open / deltaY * viewHeight;
            float solidEndY = viewHeight - close / deltaY * viewHeight;

            float upShadowStartY = viewHeight - max / deltaY * viewHeight;
            float downShadowEndY = viewHeight - min / deltaY * viewHeight;
            float shadowX = startX - klineItemWidth / 2;

            float top = Math.min(solidStartY, solidEndY);
            float bottom = Math.max(solidStartY, solidEndY);
            if (top < 0 || top > viewHeight) {
                Log.d("TEST", "top: " + top);
            }
            canvas.drawRect(solidStartX, top, solidEndX, bottom, paint);
            canvas.drawLine(shadowX, upShadowStartY, shadowX, top, paint);
            canvas.drawLine(shadowX, bottom, shadowX, downShadowEndY, paint);
            startX -= klineItemWidth;
            currentDate = currentDate.yesterday();
        }



    }

}
