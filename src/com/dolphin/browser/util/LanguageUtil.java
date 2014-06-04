
package com.dolphin.browser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import java.util.Locale;

public class LanguageUtil {

    public static final String PREF_LANGUAGE = "language";

    private static boolean sDirty = true;

    private static SharedPreferences getDefaultSharedPreferences(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void updateLanguageBySetting(Context context) {

        if (context == null || context.getResources() == null) {
            return;
        }
        updateLanguageBySetting(context, context.getResources().getConfiguration());
    }

    public static void updateLanguageBySetting(Context context, Configuration config) {

        Resources resources = context.getResources();
        String language = getLanguage(context);
        if (sDirty || !TextUtils.equals(config.locale.getLanguage(), language)) {
            config.locale = getLocale(language);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            sDirty = false;
        }
    }

    public static boolean isDirty() {
        return sDirty;
    }

    public static void makeDirty() {
        sDirty = true;
    }

    private static Locale getLocale(String localeStr) {

        String[] items = localeStr.split("_");
        if (items.length > 1) {
            return new Locale(items[0], items[1]);
        } else {
            return new Locale(localeStr);
        }
    }

    public static String getLanguage(Context context) {

        String defaultValue = Locale.getDefault().getLanguage();
        String localeStr = Locale.getDefault().toString();
        if (TextUtils.equals(localeStr, "zh_CN")
                || TextUtils.equals(localeStr, "zh_TW")) {
            defaultValue = localeStr;
        }
        return getDefaultSharedPreferences(context).getString(PREF_LANGUAGE,
                defaultValue);
    }

    public static void saveLanguage(Context context, String language) {

        PreferenceHelper.getInstance().save(
                getDefaultSharedPreferences(context).edit().putString(PREF_LANGUAGE, language));
    }
}
