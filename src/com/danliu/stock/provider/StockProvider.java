
package com.danliu.stock.provider;

import com.danliu.stock.model.TradeInfoColumns;
import com.danliu.stock.util.AppContext;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;

public class StockProvider extends ContentProvider {

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.danliu.stock";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.danliu.stock";

    public static final String AUTHORITY = "com.danliu.provider.stock";

    public static final String SCHEME = "content://";

    public static final String PATH_TRADE = "/trade";

    public static final Uri CONTENT_URI = Uri.parse(SCHEME
            + AUTHORITY + PATH_TRADE);

    public static final String TAG = "LotteryProvider";

    public static final int TRADES = 1;

    private static final String DATABASE_NAME = "stock.db";

    private static final int DATABASE_VERSION = 1;

    private static final UriMatcher sUriMatcher;

    private static final HashMap<String, String> sSSQProjectionMap;

    private DatabaseHelper mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITY, "trade", TRADES);

        sSSQProjectionMap = new HashMap<String, String>();

        sSSQProjectionMap.put(TradeInfoColumns._ID, TradeInfoColumns._ID);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_STOCK_ID, TradeInfoColumns.COLUMN_STOCK_ID);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_STOCK_NAME,
                TradeInfoColumns.COLUMN_STOCK_NAME);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_TRADE_PRICE,
                TradeInfoColumns.COLUMN_TRADE_PRICE);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_TRADE_COUNT,
                TradeInfoColumns.COLUMN_TRADE_COUNT);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_TRADE_AMOUNT,
                TradeInfoColumns.COLUMN_TRADE_AMOUNT);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_TRADE_DATE,
                TradeInfoColumns.COLUMN_TRADE_DATE);
        sSSQProjectionMap.put(TradeInfoColumns.COLUMN_BALANCE, TradeInfoColumns.COLUMN_BALANCE);

    }

    class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            // calls the super constructor, requesting the default cursor
            // factory.
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Creates the underlying database with table name and column names
         * taken from the NotePad class.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TradeInfoColumns.TABEL_NAME + " (" + TradeInfoColumns._ID
                    + " INTEGER PRIMARY KEY," + TradeInfoColumns.COLUMN_STOCK_ID + " INTEGER,"
                    + TradeInfoColumns.COLUMN_STOCK_NAME + " TEXT,"
                    + TradeInfoColumns.COLUMN_TRADE_COUNT + " INTEGER,"
                    + TradeInfoColumns.COLUMN_TRADE_AMOUNT + " INTEGER,"
                    + TradeInfoColumns.COLUMN_TRADE_DATE + " LONG,"
                    + TradeInfoColumns.COLUMN_TRADE_PRICE + " FLOAT,"
                    + TradeInfoColumns.COLUMN_BALANCE + " INTEGER" + ");");

        }

        /**
         * Demonstrates that the provider must consider what happens when the
         * underlying datastore is changed. In this sample, the database is
         * upgraded the database by destroying the existing data. A real
         * application should upgrade the database in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Logs that the database is being upgraded
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS ssq");

            // Recreates the database with a new version
            onCreate(db);
        }

    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {

            case TRADES:
                count = db.delete(TradeInfoColumns.TABEL_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TRADES:
                return CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (initialValues == null) {
            Log.e(TAG, "bad content value to insert!!" + initialValues);
            return null;
        }
        if (initialValues.containsKey(TradeInfoColumns.COLUMN_TRADE_DATE)
                && initialValues.containsKey(TradeInfoColumns.COLUMN_STOCK_ID)
                && initialValues.containsKey(TradeInfoColumns.COLUMN_BALANCE)
                && initialValues.containsKey(TradeInfoColumns.COLUMN_TRADE_AMOUNT)
                && initialValues.containsKey(TradeInfoColumns.COLUMN_TRADE_COUNT)
                && initialValues.containsKey(TradeInfoColumns.COLUMN_TRADE_PRICE)
                && initialValues.containsKey(TradeInfoColumns.COLUMN_STOCK_NAME)) {
            Cursor cursor = null;

            cursor = db.query(TradeInfoColumns.TABEL_NAME, new String[] {
                TradeInfoColumns._ID
            }, TradeInfoColumns.COLUMN_TRADE_DATE + "=" + initialValues.getAsLong(TradeInfoColumns.COLUMN_TRADE_DATE),
                    null, null, null, null);

            long rowId = 0;

            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                rowId = db.update(
                        TradeInfoColumns.TABEL_NAME,
                        initialValues,
                        TradeInfoColumns.COLUMN_TRADE_DATE + "="
                                + initialValues.getAsLong(TradeInfoColumns.COLUMN_TRADE_DATE), null);
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                rowId = db.insert(TradeInfoColumns.TABEL_NAME, null,
                        initialValues);
            }

            if (rowId > 0) {
                Uri result = ContentUris.withAppendedId(CONTENT_URI, rowId);

                getContext().getContentResolver().notifyChange(result, null);

                return result;
            }
        }
        Log.e(TAG, "bad content value to insert!!" + initialValues);
        return null;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        AppContext.init(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TradeInfoColumns.TABEL_NAME);

        switch (sUriMatcher.match(uri)) {
            case TRADES:
                qb.setProjectionMap(sSSQProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;

        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = TradeInfoColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor c = qb.query(db, // The database to query
                projection, // The columns to return from the query
                selection, // The columns for the where clause
                selectionArgs, // The values for the where clause
                null, // don't group the rows
                null, // don't filter by row groups
                orderBy // The sort order
                );

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;
        int count;
        switch (sUriMatcher.match(uri)) {

            case TRADES:
                count = db.update(TradeInfoColumns.TABEL_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

}
