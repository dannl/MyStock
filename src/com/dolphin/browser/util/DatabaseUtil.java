package com.dolphin.browser.util;

import com.danliu.util.Log;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.Locale;

public class DatabaseUtil {

    private static String LOGTAG = "DatabaseUtil";

    public static void dropTable(SQLiteDatabase db, String table) {
        String dropClause = "DROP TABLE IF EXISTS " + table;
        try {
            db.execSQL(dropClause);
        } catch (SQLException e) {
            Log.e(LOGTAG , e.getMessage());
        }
    }

    public static void dropIndex(SQLiteDatabase db, String index) {
        try {
            db.execSQL("DROP INDEX IF EXISTS " + index);
        } catch (SQLException e) {
            Log.e(LOGTAG , e.getMessage());
        }
    }

    public static void addColumn(SQLiteDatabase db, String table, String column, String type, String modifier) {
        String alterClause = String.format(Locale.US, "ALTER TABLE %s ADD COLUMN %s %s ", table, column, type);
        if (TextUtils.isEmpty(modifier)) {
            alterClause += ";";
        } else {
            alterClause += (modifier + ";");
        }

        try {
            db.execSQL(alterClause);
        } catch (SQLException e) {
            Log.e(LOGTAG , e.getMessage());
        }
    }

    public static void dropColumn(SQLiteDatabase db, String table, String column) {
        String alterClause = String.format("ALTER TABLE %s DROP COLUMN %s ;", table, column);
        try {
            db.execSQL(alterClause);
        } catch (SQLException e) {
            Log.e(LOGTAG, e.getMessage());
        }
    }

    public static byte[] parcelableToBlob(Parcelable obj) {
        if (null == obj) {
            return null;
        }
        Parcel p = Parcel.obtain();
        p.writeParcelable(obj, 0);
        byte[] blob = p.marshall();
        p.recycle();
        return blob;
    }

    public static <T extends Parcelable> Parcelable blobToParcelable(byte[] blob, ClassLoader classLoader) {
        if (null == blob) {
            return null;
        }
        Parcel p = Parcel.obtain();
        p.unmarshall(blob, 0, blob.length);
        p.setDataCapacity(blob.length);
        p.setDataSize(blob.length);
        p.setDataPosition(0);
        Parcelable obj = p.readParcelable(classLoader);
        p.recycle();
        return obj;
    }

    public static <T extends Parcelable> Parcelable blobToParcelable(byte[] blob) {
        return blobToParcelable(blob, null);
    }

}
