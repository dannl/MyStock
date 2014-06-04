package com.dolphin.browser.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void zip(String zipFile, ArrayList<String> files, String desc) {
        ZipOutputStream out = null;
        try {
            final int BUFFER = 2048;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFile);
            out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < files.size(); i++) {
                FileInputStream fi = new FileInputStream(files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i)
                        .lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            if (desc != null) {
                ZipEntry entry = new ZipEntry("info.txt");
                out.putNextEntry(entry);
                out.write(StringUtil.getUtf8OrDefaultBytes(desc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtilities.closeStream(out);
        }
    }
}
