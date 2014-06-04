package com.dolphin.browser.util;

import com.danliu.util.Log;

public class MarkLog {
    private static final String TAG = "TraceLog.MarkLog";

    private long mLastTimeStamp;
    private int mLastMark;
    private String mName;

    public MarkLog(String name) {
        mName = name;
        mark(0);
    }

    public void mark(int mark) {
        if (mark == 0) {
            mLastTimeStamp = System.currentTimeMillis();
            mLastMark = 0;
            Log.d(TAG, "[%s] start mark", mName);
            return;
        }

        long now = System.currentTimeMillis();
        Log.d(TAG, "[%s] %d to %d takes %d ms", mName, mLastMark, mark, now - mLastTimeStamp);
        mLastTimeStamp = now;
        mLastMark = mark;
    }
}
