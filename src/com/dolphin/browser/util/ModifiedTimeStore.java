package com.dolphin.browser.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.text.TextUtils;


public final class ModifiedTimeStore {

    private static final String DEFAULT_MODIFIED_TIME_NAME = "mt";
    private static final String MODIFIED_TIME_STORE = "modifiedTimeStore";

    private ModifiedTimeStore(){
    }

    public static String fixModifiedTime(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url may not be null or empty.");
        }
        return fixModifiedTime(context, Uri.parse(url), DEFAULT_MODIFIED_TIME_NAME).toString();
    }

    public static String fixModifiedTime(Context context, String url, String mtName) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url may not be null or empty.");
        }
        return fixModifiedTime(context, Uri.parse(url), mtName).toString();
    }

    public static Uri fixModifiedTime(Context context, Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        return fixModifiedTime(context, uri.buildUpon(), DEFAULT_MODIFIED_TIME_NAME).build();
    }


    public static Uri fixModifiedTime(Context context, Uri uri, String mtName) {
        if (null == uri) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        return fixModifiedTime(context, uri.buildUpon(), mtName).build();
    }

    public static Uri.Builder fixModifiedTime(Context context, Uri.Builder builder) {
        return fixModifiedTime(context, builder, DEFAULT_MODIFIED_TIME_NAME);
    }

    public static Uri.Builder fixModifiedTime(Context context, Uri.Builder builder, String mtName) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        if (null == builder) {
            throw new IllegalArgumentException("builder may not be null.");
        }
        if (TextUtils.isEmpty(mtName)) {
            return builder;
        }
        Uri uriWithMT = builder.build();
        Uri uriWithoutMT = URIUtil.removeQueryParameter(uriWithMT, mtName);
        String key = URIUtil.clearQuery(uriWithoutMT).toString();
        String value = uriWithoutMT.getQuery();
        int hash = value.hashCode();
        Uri.Builder fixedBuilder = builder;
        boolean updateHash = false;
        SharedPreferences prefs = context.getSharedPreferences(MODIFIED_TIME_STORE, Context.MODE_PRIVATE);
        if (!prefs.contains(key)) {
            updateHash = true;
        } else {
            int oldHash = prefs.getInt(key, 0);
            if (oldHash != hash) {
                updateHash = true;
                fixedBuilder = uriWithoutMT.buildUpon()
                        .appendQueryParameter(mtName, String.valueOf(0));
            }
        }
        if (updateHash) {
            Editor editor = prefs.edit()
                    .putInt(key, hash);
            PreferenceHelper.getInstance().save(editor);
        }
        return fixedBuilder;
    }
}
