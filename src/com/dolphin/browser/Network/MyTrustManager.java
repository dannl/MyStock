package com.dolphin.browser.Network;

import android.content.Context;
import com.danliu.util.Log;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MyTrustManager implements X509TrustManager{

    private X509TrustManager mStandardTrustManager = null;
    private X509TrustManager mSelfsignTrustManager = null;
    private X509Certificate[] mAcceptedIssuers = null;
    private Context mContext;
    int mResource;
    String mPassword;

    static X509TrustManager findX509TrustManager(TrustManagerFactory tmf) {
        TrustManager tms[] = tmf.getTrustManagers();
        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                return (X509TrustManager)tms[i];
            }
        }

        return null;
    }

    private X509TrustManager getSelfsignTrustManager()
            throws NoSuchAlgorithmException, KeyStoreException, IllegalStateException{
        KeyStore trustStore = new MyTrustStoreManager(mContext, mResource, mPassword).getTrustStore();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(trustStore);
        X509TrustManager xtm = findX509TrustManager(factory);
        if (null == xtm) {
            throw new IllegalStateException("Could't find X509TrustManager");
        }

        return xtm;
    }

    private X509TrustManager getStandardTrustManager(KeyStore keyStore)
            throws NoSuchAlgorithmException, KeyStoreException, IllegalStateException{
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keyStore);
        X509TrustManager xtm = findX509TrustManager(factory);
        if (null == xtm) {
            throw new IllegalStateException("Could't find X509TrustManager");
        }

        return xtm;
    }

    /**
     * Constructor for MyTrustManager.
     */
    public MyTrustManager( KeyStore keyStore, Context context, int resource, String password ) throws NoSuchAlgorithmException, KeyStoreException {
        super();

        try {
            mContext = context;
            mResource = resource;
            mPassword = password;
            mStandardTrustManager = getStandardTrustManager(keyStore);
            if (null == mStandardTrustManager) {
                throw new IllegalStateException("Could't find X509TrustManager");
            }

            mSelfsignTrustManager = getSelfsignTrustManager();
            if (null == mSelfsignTrustManager) {
                throw new IllegalStateException("Could't find X509TrustManager");
            }

            List<X509Certificate> allIssuers = new ArrayList<X509Certificate>();
            for (X509Certificate cert : mStandardTrustManager.getAcceptedIssuers()) {
                allIssuers.add(cert);
            }
            for (X509Certificate cert : mSelfsignTrustManager.getAcceptedIssuers()) {
                allIssuers.add(cert);
            }
            mAcceptedIssuers = allIssuers.toArray(new X509Certificate[allIssuers.size()]);
        } catch (GeneralSecurityException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
     */
    public void checkClientTrusted( X509Certificate[] certificates, String authType )
        throws CertificateException {
        try {
            mSelfsignTrustManager.checkClientTrusted(certificates, authType);
        } catch (CertificateException e) {
            mStandardTrustManager.checkClientTrusted( certificates, authType );
        }
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String authType)
     */
    public void checkServerTrusted( X509Certificate[] certificates, String authType )
        throws CertificateException {
        try {
            mSelfsignTrustManager.checkServerTrusted(certificates, authType);
        } catch (CertificateException e) {
            Log.i("SSL", "Not self-signed certificates.");
            mStandardTrustManager.checkServerTrusted(certificates, authType);
        }
    }

    /**
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return mAcceptedIssuers;
    }
}
