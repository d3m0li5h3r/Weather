package com.d3m0li5h3r.apps.weather.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sandeeps6 on 26-Apr-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final static int VERSION = 1;

    private final static String NAME_DATABASE = "database.db";
    private final static String NAME_TABLE = "cities";
    private final static String COLUMN_ID = "cityId";
    private final static String COLUMN_NAME = "cityName";

    public DatabaseHelper(Context context) {
        super(context, NAME_DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NAME_TABLE + "("
                + COLUMN_ID + " integer primary key, "
                + COLUMN_NAME + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(int id, String name) {
        getWritableDatabase().execSQL("insert into " + NAME_TABLE + "("
                + id + ", " + name + ")");
    }

    public Cursor fetchAllCities() {
        return getReadableDatabase().rawQuery("select * from " + NAME_TABLE, null);
    }
}
