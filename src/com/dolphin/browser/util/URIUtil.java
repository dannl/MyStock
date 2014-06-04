/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    TunnyBrowser
 *
 *    URIUtil
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  Aug 25, 2010
 *    @version: 1.0
 *
 ******************************************************************************/

package com.dolphin.browser.util;

import com.danliu.util.Log;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URIUtil of TunnyBrowser.
 *
 * @author dhu
 */
public class URIUtil {

    private static final Pattern STRIP_URL_PATTERN = Pattern.compile("^(http://)(.*?)(/$)?");

    private static Field hostField;
    private static Field portField;
    static {
        try {
            Class<URI> cls = URI.class;
            hostField = cls.getDeclaredField("host");
            hostField.setAccessible(true);
            portField = cls.getDeclaredField("port");
            portField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static URI createURI(String uriString) throws MalformedURLException {
        URI uri = URI.create(uriString);
        if (null == uri.getHost()) {
            URL url = new URL(uriString);
            try {
                hostField.set(uri, url.getHost());
                portField.set(uri, url.getPort());
            } catch (Exception e) {
            }
        }
        return uri;
    }

    public static String getScheme(String uri) {
        int index = uri != null ? uri.indexOf(':') : -1;
        String scheme = null;
        if (-1 != index) {
            scheme = uri.substring(0, index);
        }
        return scheme;
    }

    public static String getAbsoluteUrl(String baseUrl, String relativeUrl) {
        relativeUrl = decodeHtml(relativeUrl);
        String result = relativeUrl;
        try {
            result = URI.create(baseUrl).resolve(relativeUrl).toString();
        } catch (Exception e) {
            Log.e("getFullUrl" + e.toString());
        }
        return result;
    }

    public static String decodeHtml(String encodedHtml) {
        String html = encodedHtml;
        if (!TextUtils.isEmpty(encodedHtml)) {
            html = html.replace("&amp;", "&");
            html = html.replace("&#38;", "&");
            html = html.replace("&quot;", "\"");
            html = html.replace("&#34;", "\"");
        }
        return html;
    }

    /**
     * @return True iff the url is a javascript: url.
     */
    public static boolean isJavaScriptUrl(String url) {
        return (null != url) && url.startsWith("javascript:");
    }

    public static boolean isDolphinGameUrl(String url) {
        return (null != url) && url.startsWith("dolphingame:");
    }

    public static String getOriginUrlOfGameUrl(String gameUrl) {
        return "http" + gameUrl.substring(gameUrl.indexOf("://")); //Only http for now.
    }

    public static boolean isDolphinWebappUrl(String url) {
        return (null != url) && url.startsWith("dolphinwebapp:");
    }

    public static String getOriginUrlOfWebapppUrl(String webappUrl) {
        return "http" + webappUrl.substring(webappUrl.indexOf("://")); //Only http for now.
    }

    public static String getOriginUrlOfFullscreen(String fullscreenUrl){
        return "http" + fullscreenUrl.substring(fullscreenUrl.indexOf("://")); //Only http for now.
    }

    public static String getOriginUrlOfDolphinVideo(String dolphinVideoUrl){
        return "http" + dolphinVideoUrl.substring(dolphinVideoUrl.indexOf("://"));
    }

    private static final String DOLPHIN_URL_QUERY_PARAMETER = "url";
    public static String getOriginDolphinWebUrl(String dolphinUrl){
        Uri uri = Uri.parse(dolphinUrl);
        return uri.getQueryParameter(DOLPHIN_URL_QUERY_PARAMETER);
    }

    /**
     * @return True iff the url is a content: url.
     */
    public static boolean isContentUrl(String url) {
        return (null != url) && url.startsWith("content:");
    }

    public static String stripUrl(String url) {
        if (url == null) return null;
        Matcher m = STRIP_URL_PATTERN.matcher(url);
        if (m.matches() && m.groupCount() == 3) {
            return m.group(2);
        } else {
            return url;
        }
    }

    public static final String getHostName(String url) {
        try {
            Uri uri = Uri.parse(url);
            return uri.getHost();
        } catch (Exception e) {

        }
        return url;
    }

    public static final boolean equal(String url, String otherUrl) {
        if (TextUtils.equals(url, otherUrl)) {
            return true;
        }
        if (TextUtils.isEmpty(url)) {
            return false;
        } else if (TextUtils.isEmpty(otherUrl)) {
            return false;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (otherUrl.endsWith("/")) {
            otherUrl = otherUrl.substring(0, otherUrl.length() - 1);
        }
        return TextUtils.equals(url, otherUrl);
    }

    public static String getImageFilePathFromUri(Context context, Uri uri) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null.");
        }
        if (uri == null) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        if (uri.getScheme().startsWith("file")) {
            return uri.getPath();
        } else if (uri.getScheme().startsWith("content")) {
            ContentResolver contentResolver = context.getContentResolver();
            String[] project = { MediaStore.Images.Media.DATA };
            Cursor cursor = contentResolver.query(uri, project, null, null, null);
            String imagePath = null;
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                imagePath = cursor.getString(index);
            }
            if (cursor != null) {
                cursor.close();
            }
            return imagePath;
        }
        return null;
    }

    public static final HashSet<String> COMMON_DOMAIN = new HashSet<String>();
    static {
        COMMON_DOMAIN.add("com");
        COMMON_DOMAIN.add("cn");
        COMMON_DOMAIN.add("hk");
        COMMON_DOMAIN.add("net");
        COMMON_DOMAIN.add("org");
        COMMON_DOMAIN.add("info");
        COMMON_DOMAIN.add("coop");
        COMMON_DOMAIN.add("int");
        COMMON_DOMAIN.add("co");
        COMMON_DOMAIN.add("uk");
        COMMON_DOMAIN.add("ac");
        COMMON_DOMAIN.add("de");
        COMMON_DOMAIN.add("jp");
        COMMON_DOMAIN.add("fr");
        COMMON_DOMAIN.add("cc");
        COMMON_DOMAIN.add("edu");
        COMMON_DOMAIN.add("gov");
    };

    public static final String getTopLevelDomain(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        host = host.toLowerCase(java.util.Locale.US);
        String[] domains = host.split("\\.");
        if (isIPv4Address(domains)) {
            // IPv4 address should contains as a host, not domain.
            return host;
        }
        StringBuilder sb = new StringBuilder();
        int i = domains.length - 1;
        for (; i >= 0; i--) {
            String domain = domains[i];
            sb.insert(0, domain);
            if (!COMMON_DOMAIN.contains(domain)) {
                break;
            }
            if (i > 0) {
                sb.insert(0, '.');
            }
        }
        return sb.toString();
    }

    private static boolean isIPv4Address(String[] parts) {
        if (parts.length == 4) {
            for (String part : parts) {
                try {
                    int bytePart = Integer.valueOf(part);
                    if (bytePart < 0 || bytePart > 255) {
                        // Invalid IPv4 address byte
                        return false;
                    }
                } catch (Exception e) {
                    // Null, not a number, must be non-IPv4
                    return false;
                }
            }
            // All 4 parts are valid IPv4 address bytes, should be valid address
            return true;
        }
        return false;
    }

    private static final Pattern sDomainPattern = Pattern.compile("\\.");

    public static boolean isTargetDomain(String host, String domain) {

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(domain))
            return false;
        String[] parts = sDomainPattern.split(host);

        DomainValidator dv = DomainValidator.getInstance();
        for (int i = parts.length - 1; i >= 0; i--) {
            if (dv.isValidTld(parts[i])) {
                continue;
            } else {
                return domain.equals(parts[i]);
            }
        }

        return false;
    }

    /**
     * Retrieve the query parameters as a map.
     *
     * <p>If you are sure the URI doesn't contains a name with multi-values,
     *  please use {@link #getQueryParameterMap(Uri)}.</p>
     *
     * @param uri the URI object.
     * @return an name-value map for query parameters.
     */
    public static Map<String, List<String>> getQueryParameters(Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        return getQueryParameters(uri.toString());
    }

    /**
     * Retrieve the query parameters as a map.
     * <p>If you are sure the URI doesn't contains a name with multi-values,
     *  please use {@link #getQueryParameterMap(String)}.</p>
     * @param url the URL object.
     * @return an name-value map for query parameters.
     */
    public static Map<String, List<String>> getQueryParameters(String url) {
        if (null == url) {
            throw new IllegalArgumentException("url may not be null.");
        }
        Map<String, List<String>> result;
        List<NameValuePair> pairs = URLEncodedUtils.parse(URI.create(url), HTTP.UTF_8);
        result = new HashMap<String, List<String>>(pairs.size());
        for (NameValuePair pair : pairs) {
            String name = pair.getName();
            List<String> values;
            boolean isNewName;
            if (result.containsKey(name)) {
                values = result.get(name);
                isNewName = false;
            } else {
                values = new ArrayList<String>();
                isNewName = true;
            }
            values.add(pair.getValue());
            if (isNewName) {
                result.put(name, values);
            }
        }
        return result;
    }

    /**
     * Retrieve the query parameters as a map.
     *
     * <p>If there is a query name has multiple values, the last one will be stored in the map.<br/>
     * To get a list of multiple values, use {@link #getQueryParameters(Uri)} instead.</p>
     *
     * @param uri the URI object.
     * @return an name-value map for query parameters.
     */
    public static Map<String, String> getQueryParameterMap(Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        return getQueryParameterMap(uri);
    }

    /**
     * Retrieve the query parameters as a map.
     *
     * <p>If there is a query name has multiple values, the last one will be stored in the map.<br/>
     * To get a list of multiple values, use {@link #getQueryParameters(String)} instead.</p>
     *
     * @param url the URL object.
     * @return an name-value map for query parameters.
     */
    public static Map<String, String> getQueryParameterMap(String url) {
        if (null == url) {
            throw new IllegalArgumentException("url may not be null.");
        }
        Map<String, String> result;
        List<NameValuePair> pairs = URLEncodedUtils.parse(URI.create(url), HTTP.UTF_8);
        result = new HashMap<String, String>(pairs.size());
        for (NameValuePair pair : pairs) {
            result.put(pair.getName(), pair.getValue());
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Uri.Builder clearQuery(Uri.Builder builder) {
        if (null == builder) {
            throw new IllegalArgumentException("builder may not be null.");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return builder.clearQuery();
        } else {
            Uri uri = builder.build();
            return new Uri.Builder()
            .scheme(uri.getScheme())
            .authority(uri.getAuthority())
            .path(uri.getPath())
            .fragment(uri.getFragment());
        }
    }

    public static Uri clearQuery(Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        return clearQuery(uri.buildUpon()).build();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getQueryParameterNames(Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return uri.getQueryParameterNames();
        } else {
            List<NameValuePair> pairs = URLEncodedUtils.parse(URI.create(uri.toString()), HTTP.UTF_8);
            HashSet<String> names = new HashSet<String>(pairs.size());
            for (NameValuePair pair : pairs) {
                names.add(pair.getName());
            }
            return names;
        }
    }

    public static Uri removeQueryParameter(Uri uri, String nameToRemove) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        if (TextUtils.isEmpty(nameToRemove)) {
            return uri;
        }
        Uri.Builder builder;
        Set<String> names = getQueryParameterNames(uri);
        builder = clearQuery(uri.buildUpon());
        for (String name : names) {
            if (StringUtil.equalsIgnoreCase(name, nameToRemove)) {
                continue;
            }
            List<String> values = uri.getQueryParameters(name);
            for (String value : values) {
                builder.appendQueryParameter(name, value);
            }
        }
        return builder.build();
    }

}
