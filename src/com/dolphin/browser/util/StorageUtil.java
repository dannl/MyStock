/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *
 *    StorageUtil
 *
 *    @author: dhu
 *    @since:  Jun 11, 2010
 *    @version: 1.0
 *
 ******************************************************************************/
package com.dolphin.browser.util;

import com.danliu.util.Log;
import android.os.Environment;
import java.io.File;
import java.lang.reflect.Method;

/**
 * StorageUtil.
 * @author dhu, chzhong
 *
 */
public class StorageUtil {
    private static final String LOG_TAG = "StorageUtil";
    private static final boolean HAS_PHONE_STARAGE;
    private static Method sGtPhoneStorageStateMethod;
    private static Method sGetPhoneStorageDirectoryMethod;

    static {
        boolean hasPhoneStorage = false;
        try {
            final Class<Environment> cls = Environment.class;
            sGetPhoneStorageDirectoryMethod = cls.getDeclaredMethod("getPhoneStorageDirectory");
            sGetPhoneStorageDirectoryMethod.setAccessible(true);

            sGtPhoneStorageStateMethod = cls.getDeclaredMethod("getPhoneStorageState");
            sGtPhoneStorageStateMethod.setAccessible(true);
            hasPhoneStorage = true;
        } catch (final Exception e) {
            // No such method.
        }
        HAS_PHONE_STARAGE = hasPhoneStorage;
    }

    /**
     * Gets the Android storage directory.
     */
    public static File getExternalStorageDirectory() {
        File dir = null;

        if(!Environment.MEDIA_REMOVED.equals(getExternalStorageStateInternal()))
        {
            dir = Environment.getExternalStorageDirectory();
        }
        else
        {
            if(HAS_PHONE_STARAGE)
            {
                dir = getPhoneStorageDirectory();
            }
            else
            {
                dir = null;
            }
        }

        return dir;
    }


    /**
     * Gets the current state of the external storage device.
     */
    public static String getExternalStorageState() {
        String state = Environment.MEDIA_REMOVED;
        if(HAS_PHONE_STARAGE){
            if(Environment.MEDIA_REMOVED.equals(getExternalStorageStateInternal())){
                state = getPhoneStorageStateInternal();
            }
            else {
                state = Environment.getExternalStorageState();
            }
        }
        else {
            state = Environment.getExternalStorageState();
        }
        return state;
    }

    private static String getExternalStorageStateInternal() {
        return Environment.getExternalStorageState();
    }

    private static String getPhoneStorageStateInternal() {
        try {
            if(HAS_PHONE_STARAGE){
                return (String) sGtPhoneStorageStateMethod.invoke(null);
            }
        } catch (final Exception e) {
            Log.e(LOG_TAG, e);
        }
        return Environment.MEDIA_REMOVED;
    }

    private static File getPhoneStorageDirectory() {
        try {
            if(HAS_PHONE_STARAGE){
                return (File) sGetPhoneStorageDirectoryMethod.invoke(null);
            }
        } catch (final Exception e) {
            Log.e(LOG_TAG, e);
        }
        return null;
    }
}
