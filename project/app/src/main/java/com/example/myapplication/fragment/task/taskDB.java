package com.example.myapplication.fragment.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class taskDB extends SQLiteOpenHelper {
    Context ctx;
    SQLiteDatabase db;
    public static String DB_NAME = "TASK_DB";
    static String TABLE_NAME = "NAME_TABLE";
    static int VERSION = 1;

   public taskDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
        VERSION = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY, DUE_DATE TEXT, " +
                "TITLE TEXT UNIQUE, HOURS INTEGER, startDate TEXT);");
        Toast.makeText(ctx, "Table is created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(VERSION == oldVersion){
            VERSION = newVersion;
            db = getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            onCreate(db);

        }
    }

    public void insert(String dueDate, String title, Integer hours, String date) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DUE_DATE", dueDate);
        cv.put("TITLE", title );
        cv.put("HOURS", hours);
        cv.put("startDate", date);
        db.insert(TABLE_NAME, null, cv);
    }

    public void delete(String title,String date) {
        //db.delete(TABLE_NAME, "title=?",
        //new String[]{title});
        Log.d("!!!!!!",title);
        Log.d("aaaaaaaaaaa","b");
        String [] splitstuff= title.split(" -");
        title=splitstuff[0];
        String [] splitstuffround2=title.split("TASK: ");
        title=splitstuffround2[0];
        title=title.substring(1);
        //title = title.replaceAll("\\s+","");
        Log.d("!!!!!!",title);
        String deleteSQLStuff="UPDATE "+TABLE_NAME+" SET HOURS=HOURS - (HOURS/(DUE_DATE - " +
                date +" + 1)) , startDate= "+ date + "+1 WHERE title LIKE '%"+ title+"%'";
        db.execSQL(deleteSQLStuff);

        String retrieveNewTitle="SELECT TITLE FROM " +TABLE_NAME+" WHERE title LIKE '%"+ title+"%'";
        Cursor c= db.rawQuery("SELECT startDate FROM " +TABLE_NAME+" WHERE title LIKE '%"+ title+"%'",null);
        while (c.moveToNext()) {
            String Startdate = c.getString(0);
            Log.d("delete date", Startdate);
        }

        //actually deleting from teh database
        String deleteUnderZero="DELETE FROM " + TABLE_NAME+" WHERE HOURS <=0";
        db.execSQL(deleteUnderZero);
    }

    public void view() {
        db = getReadableDatabase();
        Bitmap bt = null;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);
        while(c.moveToNext()){
            byte[] imag = c.getBlob(1);
            bt = BitmapFactory.decodeByteArray(imag, 0, imag.length);
        }
    }

    public ArrayList<String> getTasksOfDay (String date){
        db = getReadableDatabase();
        ArrayList<String> tasks = new ArrayList<String>();
        Bitmap bt = null;

        //getting the first day
        String qu = "select TITLE, HOURS, startDate from " + TABLE_NAME +" where DUE_DATE=" + date +
                " AND HOURS >= 1 AND "+ date +" >= startDate ORDER BY HOURS DESC";
        Cursor cur = db.rawQuery(qu, null);
        int count = 0;
        while(cur.moveToNext()){
            String next = "Task: " + cur.getString(0) + " --- Time: " +
                    cur.getString(1) + " Hours";
            String testicle = cur.getString(2);
            Log.d("TTTTTTTT", testicle);
            tasks.add(next);
            count++;
        }

        cur.close();



        //Need to edit this somehow.  We can now see things in the weekview that are due in the
        //  next month but displaying how many hours to work on activity in dailyCheckList due the next month
        //  do not appear.

//        getting rest of the week
        qu = "select TITLE, (HOURS/(DUE_DATE - "+ date +" + 1)) from " +
                TABLE_NAME +" where DUE_DATE > " + date + " AND DUE_DATE <=" +
                " "+ date + " + 14 " + " AND (HOURS/(DUE_DATE - "+ date +" + 1)) >= 1 AND "+ date+ " >= startDate" +
                " ORDER BY HOURS DESC, DUE_DATE DESC";
        Cursor cu = db.rawQuery(qu, null);
        count = 0;
        while(cu.moveToNext()){

            String next = "Task: " + cu.getString(0) + " --- Time: " +
                    cu.getString(1) + " Hours";
            tasks.add(next);
            count++;
        }

        cu.close();


        return tasks;
    }

    public ArrayList<String> getTasksOfWeek (String date){
        db = getReadableDatabase();
        ArrayList<String> tasks = new ArrayList<String>();
        Bitmap bt = null;

        //getting the first day
        String qu = "select TITLE, DUE_DATE, MAX(HOURS) from " + TABLE_NAME +" where DUE_DATE=" + date +
                " AND HOURS >= 1 AND "+ date +" >= startDate GROUP BY TITLE, DUE_DATE ORDER BY HOURS DESC";
        Cursor cur = db.rawQuery(qu, null);
        int count = 0;
        while(cur.moveToNext()){
            String duesDates = cur.getString(1);
            String dateDay = duesDates.substring(6);
            String wordMonth = "January";
            String dateMonth = duesDates.substring(4, 6);
            if(dateMonth.equals("02"))
                wordMonth = "February";
            if(dateMonth.equals("03"))
                wordMonth = "March";
            if(dateMonth.equals("04"))
                wordMonth = "April";
            if(dateMonth.equals("05"))
                wordMonth = "May";
            if(dateMonth.equals("06"))
                wordMonth = "June";
            if(dateMonth.equals("07"))
                wordMonth = "July";
            if(dateMonth.equals("08"))
                wordMonth = "August";
            if(dateMonth.equals("09"))
                wordMonth = "September";
            if(dateMonth.equals("10"))
                wordMonth = "October";
            if(dateMonth.equals("11"))
                wordMonth = "November";
            if(dateMonth.equals("12"))
                wordMonth = "December";
            String next = "Task: " + cur.getString(0) + " --- Due " + wordMonth + " " + dateDay;
            tasks.add(next);
            count++;
        }

        cur.close();

        return tasks;
    }



    public ArrayList<Integer> getHoursOfDay (String date){
        db = getReadableDatabase();
        ArrayList<Integer> tasks = new ArrayList<Integer>();
        Bitmap bt = null;

        //getting the first day
        String qu = "select HOURS from " + TABLE_NAME +" where DUE_DATE=" + date +
                " AND HOURS >= 1 AND "+ date +" >= startDate ORDER BY HOURS DESC";
        Cursor cur = db.rawQuery(qu, null);
        int count = 0;
        while(cur.moveToNext()){
            Integer next = cur.getInt(0);
            tasks.add(next);
            count++;
        }

        cur.close();


        for(int i = 0; i <= 14; i++ ){
            int temp = Integer.parseInt(date);
            temp = temp + 1;
            date = Integer.toString(temp);


            String monthNum = date.substring(4,6);
            String dayNum = date.substring(6);
            String yearNum = date.substring(0,4);

            boolean leapYear = false;
            int nextLeapYear = 2016;

            while (Integer.parseInt(yearNum) > nextLeapYear) {
                nextLeapYear = nextLeapYear + 4;
            }
            if(nextLeapYear == Integer.parseInt(yearNum))
                leapYear = true;

            if(monthNum.equals("01") && dayNum.equals("32")){
                monthNum = "02";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("29") && leapYear == false){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("30") && leapYear == true){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("03") && dayNum.equals("32")){
                monthNum = "04";
                dayNum = "01";
            }
            if(monthNum.equals("04") && dayNum.equals("31")){
                monthNum = "05";
                dayNum = "01";
            }
            if(monthNum.equals("05") && dayNum.equals("32")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("06") && dayNum.equals("31")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("07") && dayNum.equals("32")){
                monthNum = "08";
                dayNum = "01";
            }
            if(monthNum.equals("08") && dayNum.equals("32")){
                monthNum = "09";
                dayNum = "01";
            }
            if(monthNum.equals("09") && dayNum.equals("31")){
                monthNum = "10";
                dayNum = "01";
            }
            if(monthNum.equals("10") && dayNum.equals("32")){
                monthNum = "11";
                dayNum = "01";
            }
            if(monthNum.equals("11") && dayNum.equals("31")){
                monthNum = "12";
                dayNum = "01";
            }
            if(monthNum.equals("12") && dayNum.equals("32")){
                monthNum = "01";
                dayNum = "01";
                yearNum = Integer.toString(Integer.parseInt(yearNum) + 1);
            }

            date = yearNum + monthNum + dayNum;

            Log.d("3333333", "pause");
            Log.d("$$$$", date);
            Log.d("4444444", "pause");



            qu = "select HOURS from " + TABLE_NAME + " where DUE_DATE=" + date +
                    " AND HOURS >= 1 AND " + date + " >= startDate ORDER BY HOURS DESC";
            cur = db.rawQuery(qu, null);
            count = 0;
            while (cur.moveToNext()) {
                Integer next = cur.getInt(0);
                tasks.add(next);
                count++;
            }
            cur.close();
        }
        return tasks;
    }

    public ArrayList<String> getTitlesOfDay (String date){
        db = getReadableDatabase();
        ArrayList<String> tasks = new ArrayList<String>();
        Bitmap bt = null;

        //getting the first day
        String qu = "select TITLE from " + TABLE_NAME +" where DUE_DATE=" + date +
                " AND HOURS >= 1 AND "+ date +" >= startDate ORDER BY HOURS DESC";
        Cursor cur = db.rawQuery(qu, null);
        int count = 0;
        while(cur.moveToNext()){
            String next = cur.getString(0);
            tasks.add(next);
            count++;
        }

        cur.close();


        for(int i = 0; i <= 14; i++ ){
            int temp = Integer.parseInt(date);
            temp = temp + 1;
            date = Integer.toString(temp);

            String monthNum = date.substring(4,6);
            String dayNum = date.substring(6);
            String yearNum = date.substring(0,4);

            boolean leapYear = false;
            int nextLeapYear = 2016;

            while (Integer.parseInt(yearNum) > nextLeapYear) {
                nextLeapYear = nextLeapYear + 4;
            }
            if(nextLeapYear == Integer.parseInt(yearNum))
                leapYear = true;

            if(monthNum.equals("01") && dayNum.equals("32")){
                monthNum = "02";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("29") && leapYear == false){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("02") && dayNum.equals("30") && leapYear == true){
                monthNum = "03";
                dayNum = "01";
            }
            if(monthNum.equals("03") && dayNum.equals("32")){
                monthNum = "04";
                dayNum = "01";
            }
            if(monthNum.equals("04") && dayNum.equals("31")){
                monthNum = "05";
                dayNum = "01";
            }
            if(monthNum.equals("05") && dayNum.equals("32")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("06") && dayNum.equals("31")){
                monthNum = "06";
                dayNum = "01";
            }
            if(monthNum.equals("07") && dayNum.equals("32")){
                monthNum = "08";
                dayNum = "01";
            }
            if(monthNum.equals("08") && dayNum.equals("32")){
                monthNum = "09";
                dayNum = "01";
            }
            if(monthNum.equals("09") && dayNum.equals("31")){
                monthNum = "10";
                dayNum = "01";
            }
            if(monthNum.equals("10") && dayNum.equals("32")){
                monthNum = "11";
                dayNum = "01";
            }
            if(monthNum.equals("11") && dayNum.equals("31")){
                monthNum = "12";
                dayNum = "01";
            }
            if(monthNum.equals("12") && dayNum.equals("32")){
                monthNum = "01";
                dayNum = "01";
                yearNum = Integer.toString(Integer.parseInt(yearNum) + 1);
            }

            date = yearNum + monthNum + dayNum;

            Log.d("3333333", "pause");
            Log.d("$$$$", date);
            Log.d("4444444", "pause");


            //  -----------------------------------
            //  -----------------------------------
            //  -----------------------------------

            qu = "select TITLE, HOURS, startDate from " + TABLE_NAME + " where DUE_DATE=" + date +
                    " AND HOURS >= 1 AND " + date + " >= startDate ORDER BY HOURS DESC";
            cur = db.rawQuery(qu, null);
            count = 0;
            while (cur.moveToNext()) {
                String next = cur.getString(0);
                tasks.add(next);
                count++;
            }
            cur.close();
        }


        return tasks;
    }
}