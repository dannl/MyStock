/*******************************************************************************
 *
 *    Copyright (c) Baina Info Tech Co. Ltd
 *
 *    DolphinCoreLibrary
 *
 *    ProcessUtil
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  Mar 26, 2013
 *    @version: 1.0
 *
 ******************************************************************************/
package com.dolphin.browser.util;

import com.danliu.util.Log;
import android.text.TextUtils;
import java.io.File;
import java.io.FileFilter;

/**
 * ProcessUtil of DolphinCoreLibrary.
 * @author dhu
 *
 */
public class ProcessUtil {

    private static final String SEARCH_PATTERN = "Name:\\s+%s\\b[\\s\\S]*\\bUid:\\s+%d\\s+[\\s\\S]*";
    /**
     * Kill all process matches specified pattern and uid is same with current process's.
     * @param processNamePattern
     */
    public static void killAllProcessWithOurUid(String processNamePattern) {
        File proc = new File("/proc");
        int myUid = android.os.Process.myUid();
        File[] pids = proc.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.canRead() && pathname.getName().matches("\\d+");
            }
        });
        if (pids != null) {
            String pattern = String.format(SEARCH_PATTERN, processNamePattern, myUid);
            for (int i = 0; i < pids.length; i++) {
                File file = pids[i];
                int pid = Integer.parseInt(file.getName());
                if (isFileContentMatches(file, pattern)) {
                    Log.d("Kill process " + pid);
                    android.os.Process.killProcess(pid);
                }
            }
        }
    }

    private static boolean isFileContentMatches(File procFile, String pattern) {
        File file = new File(procFile, "status");
        if (!file.exists() || !file.canRead()) {
            return false;
        }
        String content = IOUtilities.readFileText(file.getPath());
        if (!TextUtils.isEmpty(content) && content.matches(pattern)) {
            return true;
        }
        return false;
    }
}
