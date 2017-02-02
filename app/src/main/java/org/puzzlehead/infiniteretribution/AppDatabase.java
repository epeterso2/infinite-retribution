package org.puzzlehead.infiniteretribution;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by epeterson on 9/7/2016.
 */
public class AppDatabase extends SQLiteOpenHelper
{
    public interface Listener
    {
        public void onChange();
    }

    protected static Context context = null;

    protected static AppDatabase instance = null;

    protected static final String DATABASE_NAME = "retribution.db";
    protected static final int DATABASE_VERSION = 1;

    protected static final String TABLE_TARGETS = "targets";
    protected static final String COLUMN_ID = "id";
    protected static final String COLUMN_NAME = "name";
    protected static final String COLUMN_COUNT = "count";
    protected static final String WHERE_ID_EQUALS = "id = ?";

    protected static final String[] PROJECTION = new String[] { COLUMN_ID, COLUMN_NAME, COLUMN_COUNT };
    protected static final int COLUMN_ID_INDEX = 0;
    protected static final int COLUMN_NAME_INDEX = 1;
    protected static final int COLUMN_COUNT_INDEX = 2;

    protected List<Listener> listeners = new ArrayList<Listener>();

    public static synchronized void setContext(Context context)
    {
        AppDatabase.context = context;
    }

    public static synchronized AppDatabase getInstance()
    {
        if (instance == null)
        {
            instance = new AppDatabase(context);
        }

        return instance;
    }

    public AppDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized void addListener(Listener listener)
    {
        if (listener != null && ! listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        Log.d(getClass().getSimpleName(), "onCreate()");

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_TARGETS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_COUNT + " INTEGER" +
                        ")");

        notifyListeners();
    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        Log.d(getClass().getSimpleName(), "onUpgrde()");

        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_TARGETS);
        onCreate(getWritableDatabase());

        notifyListeners();
    }

    public synchronized long createTarget(Target target)
    {
        if (target == null)
        {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, target.getName());
        values.put(COLUMN_COUNT, target.getCount());

        target.setId(getWritableDatabase().insert(TABLE_TARGETS, null, values));
        notifyListeners();

        return target.getId();
    }

    public synchronized Target getTarget(long id)
    {
        Cursor cursor = getWritableDatabase().query(TABLE_TARGETS, PROJECTION, WHERE_ID_EQUALS, getIdArgument(id), null, null, null);
        cursor.moveToFirst();

        Target target = cursor.getCount() == 1 ? getTarget(cursor) : null;

        cursor.close();

        return target;
    }

    public synchronized List<Target> getTargets()
    {
        List<Target> targets = new ArrayList<Target>();

        Cursor cursor = getWritableDatabase().query(TABLE_TARGETS, PROJECTION, null, null, null, null,
                COLUMN_COUNT + " DESC, " + COLUMN_NAME + " ASC");

        for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext())
        {
            targets.add(getTarget(cursor));
        }

        cursor.close();

        return targets;
    }

    public synchronized int updateTarget(Target target)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, target.getName());
        values.put(COLUMN_COUNT, target.getCount());

        int result = target == null ? 0 : getWritableDatabase().update(TABLE_TARGETS, values, WHERE_ID_EQUALS, getIdArgument(target.getId()));
        notifyListeners();

        return result;
    }

    public synchronized int deleteTarget(long id)
    {
        int result = getWritableDatabase().delete(TABLE_TARGETS, WHERE_ID_EQUALS, getIdArgument(id));
        notifyListeners();

        return result;
    }

    public synchronized int deleteTarget(Target target)
    {
        return target == null ? 0 : deleteTarget(target.getId());
    }

    protected String[] getIdArgument(long id)
    {
        return new String[] { Long.toString(id) };
    }

    protected Target getTarget(Cursor cursor)
    {
        return new Target(cursor.getLong(COLUMN_ID_INDEX), cursor.getString(COLUMN_NAME_INDEX), cursor.getLong(COLUMN_COUNT_INDEX));
    }

    protected synchronized void notifyListeners()
    {
        for (Listener listener: listeners)
        {
            listener.onChange();
        }
    }
}