/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    MyStock
 *
 *    AppContext
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  May 30, 2014
 *    @version: 1.0
 *
 ******************************************************************************/
package com.danliu.stock.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;

/**
 * AppContext of MyStock.
 * @author danliu
 *
 */
public class AppContext extends ContextWrapper {

    private static AppContext sInstance;

    public static void init(Context context) {
        if (sInstance == null) {
            try {
                sInstance = new AppContext(context.createPackageContext(
                        context.getPackageName(), Context.CONTEXT_INCLUDE_CODE));
            } catch (NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static AppContext getInstance() {
        return sInstance;
    }


    private int mTargetSdkVersion;

    private AppContext(Context context) {
        super(context);
        ApplicationInfo appInfo = context.getApplicationInfo();
        mTargetSdkVersion = appInfo.targetSdkVersion;
    }

    public int getTargetSdkVersion() {
        return mTargetSdkVersion;
    }

    public static void checkThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalAccessError(
                    "this method should be called in main thread");
        }
    }
}
