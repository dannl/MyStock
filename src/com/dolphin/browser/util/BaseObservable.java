/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    DolphinCoreLibrary_addon
 *
 *    BaseObservable
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  May 23, 2011
 *    @version: 1.0
 *
 ******************************************************************************/
package com.dolphin.browser.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * BaseObservable of DolphinCoreLibrary_addon.
 * @author dhu
 *
 */
public class BaseObservable<T> {

    private final WeakHashMap<T, Boolean> mListeners;

    public BaseObservable() {
        mListeners = new WeakHashMap<T, Boolean>();
    }

    public synchronized void addListener(T listener) {
        if (!mListeners.containsKey(listener)) {
            mListeners.put(listener, true);
        }
    }

    public synchronized void removeListener(T listener) {
        mListeners.remove(listener);
    }

    public synchronized Iterator<T> getListeners() {
        final Set<T> set = new HashSet<T>(mListeners.keySet());
        final Iterator<T> iter = set.iterator();
        return iter;
    }
}
