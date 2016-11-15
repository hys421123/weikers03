package com.team.witkers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hys on 2016/8/31.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String CREATE_BOOK="create table book("
            + "id integer primary key autoincrement,"
            +"author text,"
            +"price real )";
    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
