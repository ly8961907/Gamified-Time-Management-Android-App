package com.example.myapplication.fragment.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    Context ctx;
    SQLiteDatabase db;
    public static String DB_NAME = "T_DB";
    static String TABLE_NAME = "NAME_TABLE";
    static int VERSION = 1;

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
        VERSION = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY, " +
                " im INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(int im) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("im", im);
        db.insert(TABLE_NAME, null, cv);
    }
    public ArrayList<Integer> get(){
        db = getReadableDatabase();
        ArrayList<Integer> ims = new ArrayList<Integer>();
        String qu = "select im from " + TABLE_NAME ;
        Cursor cur = db.rawQuery(qu, null);
        int count = 0;
        while(cur.moveToNext()){
            Integer next = cur.getInt(0);
            ims.add(next);
            count++;
        }
        cur.close();
        return ims;
    }
}