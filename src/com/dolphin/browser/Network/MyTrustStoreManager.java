package com.dolphin.browser.Network;

import java.io.InputStream;
import java.security.KeyStore;
import com.danliu.util.Log;
import android.content.Context;

public class MyTrustStoreManager {
    private static final String TAG = "MyTrustStoreManager";

    private Context mContext = null;
    private int mResource = 0;
    private char[]  mPassword = null;

    public MyTrustStoreManager(Context context, int resource, String password) {
        mContext = context;
        mResource = resource;
        mPassword = password.toCharArray();
    }

    public KeyStore getTrustStore(int resource, char[] password) {
        if (null == mContext) {
            return null;
        }

        KeyStore trustStore = null;

        try {
            trustStore = KeyStore.getInstance("BKS");
            InputStream stream = mContext.getResources().openRawResource(resource);
            trustStore.load(stream, password);
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "failed to load truststore!");
        }

        return trustStore;
    }

    public KeyStore getTrustStore() {
        if (0 == mResource || null == mPassword) {
            return null;
        }

        return getTrustStore(mResource, mPassword);
    }
}
