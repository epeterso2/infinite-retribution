package org.puzzlehead.infiniteretribution;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Eric on 9/7/2016.
 */
public class TargetContentProvider extends ContentProvider
{
    protected static final String DATABASE_NAME = "appdb";

    protected static final int DATABASE_VERSION = 1;

    protected static final String TABLE_TARGET = "target";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_UNITS = "units";

    protected static final String AUTHORITY = "org.puzzlehead.infiniteretribution";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_TARGET);

    protected static final int TARGET_BY_ID = 1;

    protected static final int ALL_TARGETS = 2;

    protected UriMatcher uriMatcher = null;

    protected SQLiteDatabase db = null;

    protected Context context;

    public TargetContentProvider(Context context)
    {
        this.context = context;
    }

    @Override
    public boolean onCreate()
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TABLE_TARGET, ALL_TARGETS);
        uriMatcher.addURI(AUTHORITY, TABLE_TARGET + "/#", TARGET_BY_ID);

        db = new DBOpenHelper(context).getWritableDatabase();

        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_TARGET);

        switch (uriMatcher.match(uri))
        {
            case TARGET_BY_ID:
                qb.appendWhere( COLUMN_ID + " = " + uri.getPathSegments().get(1));
                break;

            case ALL_TARGETS:
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        Target target = new Target();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    protected class DBOpenHelper extends SQLiteOpenHelper
    {
        public DBOpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE " + TABLE_TARGET +
                    " ( " +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                    ", " +
                    COLUMN_NAME + " TEXT" +
                    ", " +
                    COLUMN_UNITS + " units INTEGER" +
                    " )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }
    }
}