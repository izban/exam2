package ru.ifmo.md.exam1.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import ru.ifmo.md.exam1.BuildConfig;
import ru.ifmo.md.exam1.provider.playlists.PlaylistsColumns;
import ru.ifmo.md.exam1.provider.song.SongColumns;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = MySQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "data.db";
    private static final int DATABASE_VERSION = 22;
    private static MySQLiteOpenHelper sInstance;
    private final Context mContext;
    private final MySQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_PLAYLISTS = "CREATE TABLE IF NOT EXISTS "
            + PlaylistsColumns.TABLE_NAME + " ( "
            + PlaylistsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PlaylistsColumns.NAME + " TEXT "
            + " );";

    public static final String SQL_CREATE_TABLE_SONG = "CREATE TABLE IF NOT EXISTS "
            + SongColumns.TABLE_NAME + " ( "
            + SongColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SongColumns.ARTIST + " TEXT, "
            + SongColumns.SONG + " TEXT, "
            + SongColumns.URL + " TEXT, "
            + SongColumns.DURATION + " TEXT, "
            + SongColumns.POPULARITY + " TEXT, "
            + SongColumns.GENRES_MASK + " TEXT, "
            + SongColumns.YEAR + " INTEGER "
            + " );";

    // @formatter:on

    public static MySQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static MySQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */

    private static MySQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new MySQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    private MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mOpenHelperCallbacks = new MySQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static MySQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new MySQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new MySQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_PLAYLISTS);
        db.execSQL(SQL_CREATE_TABLE_SONG);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
