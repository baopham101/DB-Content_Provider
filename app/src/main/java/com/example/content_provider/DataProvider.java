package com.example.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class DataProvider extends ContentProvider {
    static final String AUTHORITY = "com.example.content_provider.DataProvider";
    static final String CONTENT_PATH = "backupdata";
    static final String URL = "content://" + AUTHORITY + "/" + CONTENT_PATH;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String VALUE = "_value";

    private static HashMap<String, String> STUDENT_LIST_MAP;

    static final int URI_ALL_ITEMS_CODE = 1;
    static final int URI_ONE_ITEM_CODE = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_ALL_ITEMS_CODE);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_ONE_ITEM_CODE);
    }

    // Database specific constant declaration
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "SampleDatabase";
    static final String TABLE_NAME = "Student";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + " _value TEXT NOT NULL);";

    /*
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /*
         * Create a writable database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * Add a new student record
         */
        long rowID = db.insert(TABLE_NAME, "", values);

        /*
         * If record is added successfully
         */

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                qb.setProjectionMap(STUDENT_LIST_MAP);
                break;

            case URI_ONE_ITEM_CODE:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }
        if (sortOrder == null || sortOrder.equals("")) {
            /*
             * By default sort on student names
             */
            sortOrder = VALUE;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        /*
         * Register To Watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case URI_ONE_ITEM_CODE:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NAME, _ID + " = " + id + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;

            case URI_ONE_ITEM_CODE:
                count = db.update(TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            /*
             * Get all student records
             * */

            case URI_ALL_ITEMS_CODE:
                return "vnd.android.cursor.dir/vnd.example.backupdata";
            /*
             * Get a particular student
             * */
            case URI_ONE_ITEM_CODE:
                return "vnd.android.cursor.item/vnd.example.backupdata";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
