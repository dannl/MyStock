
package com.dolphin.browser.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {

    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_3G = "3g";
    public static final String NETWORK_TYPE_4G = "4g";
    private static long sLastWifiRequestUpdate;
    private static boolean sWifiConnected;
    private static long sLastMobileRequestUpdate;
    private static boolean sMobileConnected;
    private static final long sRequestTimeOut = 20 * 1000;

    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static boolean isGoodNetwork(Context context){

        final String netWorkType = getNetworkType(context);
        boolean answer = (netWorkType == NetworkUtil.NETWORK_TYPE_WIFI) ||
                         (netWorkType == NetworkUtil.NETWORK_TYPE_3G) ||
                         (netWorkType == NetworkUtil.NETWORK_TYPE_4G);
        return answer;
    }

    public static boolean isWifiConnected(Context context) {
        if (System.currentTimeMillis() - sLastWifiRequestUpdate > sRequestTimeOut) {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connMgr.getActiveNetworkInfo();
            if (null != ni) {
                sWifiConnected = ni.getType() == ConnectivityManager.TYPE_WIFI;
            }
            sLastWifiRequestUpdate = System.currentTimeMillis();
        }
        return sWifiConnected;
    }

    public static boolean isMobileConnected(Context context) {
        if (System.currentTimeMillis() - sLastMobileRequestUpdate > sRequestTimeOut) {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connMgr.getActiveNetworkInfo();
            if (null != ni) {
                sMobileConnected = ni.getType() == ConnectivityManager.TYPE_MOBILE;
            }
            sLastMobileRequestUpdate = System.currentTimeMillis();
        }
        return sMobileConnected;
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMgr.getActiveNetworkInfo();
        if (null != ni) {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_TYPE_WIFI;
            }
            if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (ni.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORK_TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORK_TYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_TYPE_4G;
                    default:
                        return NETWORK_TYPE_2G;
                }
            }
        }
        return NETWORK_TYPE_UNKNOWN;
    }
}
