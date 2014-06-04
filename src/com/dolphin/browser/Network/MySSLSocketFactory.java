package com.dolphin.browser.Network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

public class MySSLSocketFactory implements LayeredSocketFactory {

    private SSLContext sslcontext = null;
    private X509HostnameVerifier hostnameVerifier = BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    private MyTrustManager myTrustManager = null;
    //private X509HostnameVerifier hostnameVerifier = new MyHostnameVerify();

    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();

    public MySSLSocketFactory(Context context, int resource, String password)
            throws NoSuchAlgorithmException, KeyStoreException {
        myTrustManager = new MyTrustManager(null, context, resource, password);
    }

    private SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = HttpRequester.createSSLConext();
            context.init(null, new TrustManager[] { myTrustManager }, null);
            return context;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }
    /**
     * @see org.apache.http.conn.scheme.SocketFactory#connectSocket(java.net.Socket,
     *      java.lang.String, int, java.net.InetAddress, int,
     *      org.apache.http.params.HttpParams)
     */
    public Socket connectSocket(Socket sock, String host, int port,
                    InetAddress localAddress, int localPort, HttpParams params)
                    throws IOException, UnknownHostException, ConnectTimeoutException {
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {
                // we need to bind explicitly
                if (localPort < 0) {
                        localPort = 0; // indicates "any"
                }
                InetSocketAddress isa = new InetSocketAddress(localAddress,
                                localPort);
                sslsock.bind(isa);
        }

        sslsock.connect(remoteAddress, connTimeout);
        sslsock.setSoTimeout(soTimeout);

        try {
            hostnameVerifier.verify(host, sslsock);
        } catch (IOException iox) {
            //close the socket before re-throwing the exception
            try { sslsock.close(); } catch (Exception e) { /* Ignore */ }
            throw iox;
        }

        return sslsock;
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#createSocket()
     */
    public Socket createSocket() throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket)
     */
    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return true;
    }

    /**
     * @see org.apache.http.conn.scheme.LayeredSocketFactory#createSocket(java.net.Socket,
     *      java.lang.String, int, boolean)
     */
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket)getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        hostnameVerifier.verify(host, sslSocket);
        return sslSocket;
    }

    // -------------------------------------------------------------------
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :
    // Both Object.equals() and Object.hashCode() must be overridden
    // for the correct operation of some connection managers
    // -------------------------------------------------------------------

    public boolean equals(Object obj) {
        return ((obj != null) && (obj.getClass() == this.getClass()));
    }

    public int hashCode() {
        return MySSLSocketFactory.class.hashCode();
    }

    public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        if (null == hostnameVerifier) {
            throw new IllegalArgumentException("Hostname verifier may not be null");
        } else {
            this.hostnameVerifier = hostnameVerifier;
        }
    }

    static class MyHostnameVerify extends AbstractVerifier {

        @Override
        public void verify(String host, String[] cns, String[] subjectAlts)
                throws SSLException {
            // Ignore
            //super.verify(host, cns, subjectAlts, false);
        }

    }

}
