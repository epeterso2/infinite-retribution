package org.puzzlehead.infiniteretribution;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by epeterson on 8/31/2016.
 */
public class AppDatabase extends SQLiteOpenHelper
{
    public enum TargetOrder
    {
        NAME_ASC,
        NAME_DESC,
        UNITS_ASC,
        UNITS_DESC;
    }

    protected static AppDatabase instance = null;

    protected static Context context = null;

    public static synchronized void setContext(Context context)
    {
        AppDatabase.context = context;
    }

    public static synchronized AppDatabase getInstance()
    {
        if (instance != null)
        {
            instance = new AppDatabase(context);
        }

        return instance;
    }

    public interface OnChangeListener
    {
        public void onChange();
    }

    protected List<OnChangeListener> listeners = new ArrayList<OnChangeListener>();

    public synchronized void addOnChangeListener(OnChangeListener listener)
    {
        if (listener != null && ! listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public synchronized void removeOnChangeListener(OnChangeListener listener)
    {
        listeners.remove(listener);
    }

    protected synchronized void notifyOnChangeListeners()
    {
        for (OnChangeListener listener: listeners)
        {
            listener.onChange();
        }
    }

    protected static final String DATABASE_NAME = "appdb";

    protected static final int DATABASE_VERSION = 1;

    protected static final String TABLE_TARGET = "target";

    protected static final String KEY_ID = "id";

    protected static final String KEY_NAME = "name";

    protected static final String KEY_UNITS = "units";

    protected static final String ID_WHERE_CLAUSE = "id = ?";

    protected static final String[] TABLE_TARGET_COLUMNS = new String[] { KEY_ID, KEY_NAME, KEY_UNITS };

    public AppDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_TARGET +
                " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " +
                KEY_NAME + " TEXT" +
                ", " +
                KEY_UNITS + " units INTEGER" +
                " )");

        notifyOnChangeListeners();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public long addTarget(Target target)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, target.getName());
        values.put(KEY_UNITS, target.getUnits());

        long id = db.insert(TABLE_TARGET, null, values);

        if (id != -1)
        {
            target.setId(id);
        }

        db.close();
        notifyOnChangeListeners();

        return id;
    }

    protected Target createTarget(Cursor cursor)
    {
        Target target = new Target();
        target.setId(cursor.getLong(0));
        target.setName(cursor.getString(1));
        target.setUnits(cursor.getLong(2));

        return target;
    }

    public Target getTarget(long id)
    {
        Target target = null;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TARGET, TABLE_TARGET_COLUMNS, ID_WHERE_CLAUSE, new String[] { Long.toString(id) }, null, null, null);

        if (cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            target = createTarget(cursor);
        }

        db.close();

        return target;
    }

    public List<Target> getTargets()
    {
        return getTargets(TargetOrder.NAME_ASC);
    }

    public List<Target> getTargets(TargetOrder order)
    {
        List<Target> targets = new ArrayList<Target>();

        String orderString = null;

        switch (order)
        {
            case NAME_ASC:
                orderString = KEY_NAME + " ASC, " + KEY_UNITS + " DESC";
                break;

            case NAME_DESC:
                orderString = KEY_NAME + " DESC, " + KEY_UNITS + " DESC";
                break;

            case UNITS_ASC:
                orderString = KEY_UNITS + " ASC, " + KEY_NAME + " ASC";
                break;

            case UNITS_DESC:
                orderString = KEY_UNITS + " DESC, " + KEY_NAME + " ASC";
                break;
        }

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TARGET, TABLE_TARGET_COLUMNS, null, null, null, null, orderString);

        while (cursor.moveToNext())
        {
            targets.add(createTarget(cursor));
        }

        db.close();

        return targets;
    }

    public void updateTarget(Target target)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, target.getName());
        values.put(KEY_UNITS, target.getUnits());

        db.update(TABLE_TARGET, values, ID_WHERE_CLAUSE, new String[] { Long.toString(target.getId()) } );
        notifyOnChangeListeners();
    }

    public void deleteTarget(Target target)
    {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_TARGET, ID_WHERE_CLAUSE, new String[] { Long.toString(target.getId()) } );
        notifyOnChangeListeners();
    }
}