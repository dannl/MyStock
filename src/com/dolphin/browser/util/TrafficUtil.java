
package com.dolphin.browser.util;

import android.net.TrafficStats;
import android.os.Build;
import java.util.HashMap;

public class TrafficUtil {
    private long mStart;
    private int mUid;

    private static final HashMap<String, TrafficUtil> sTrafficMap = new HashMap<String, TrafficUtil>();

    TrafficUtil(String message, int uid) {

        if (isFroyoOrHigher()) {
            mStart = TrafficStats.getUidTxBytes(uid) + TrafficStats.getUidTxBytes(uid);
        }
        mUid = uid;
    }

    public static TrafficUtil start(String tag, int uid) {

        final TrafficUtil trafficUtil = new TrafficUtil(tag, uid);
        sTrafficMap.put(tag, trafficUtil);
        return trafficUtil;
    }

    public static long end(String tag) {

        long trafficConsume = 0;
        final TrafficUtil trafficUtil = sTrafficMap.remove(tag);
        if (trafficUtil != null) {
            trafficConsume = trafficUtil.end();
        }
        return trafficConsume;
    }

    private long end() {
        long end = 0;
        if (isFroyoOrHigher()) {
            end = TrafficStats.getUidTxBytes(mUid) + TrafficStats.getUidTxBytes(mUid);
        }
        final long trafficConsume = end - mStart;
        return trafficConsume;
    }

    private boolean isFroyoOrHigher(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
}
