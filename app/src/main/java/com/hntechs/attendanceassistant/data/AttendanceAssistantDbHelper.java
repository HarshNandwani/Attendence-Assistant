package com.hntechs.attendanceassistant.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;


public class AttendanceAssistantDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AttendanceAssistant.db";

    public AttendanceAssistantDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_DAILYATTENDANCE_TABLE = "CREATE TABLE "+ AttendanceEntry.TABLE_DAILY_ATTENDANCE +"(" +
                AttendanceEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                AttendanceEntry.COLUMN_DATE + " string UNIQUE, "+
                AttendanceEntry.COLUMN_CLASSESPRESENT+" int NOT NULL, " +
                AttendanceEntry.COLUMN_TOTAL_CLASSES+" int NOT NULL, "+
                AttendanceEntry.COLUMN_EXTRAINFO+" string);";

        String SQL_CREATE_BATCHATTENDANCE_TABLE = "CREATE TABLE "+AttendanceEntry.TABLE_BATCH_ATTENDANCE +"("+
                AttendanceEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                AttendanceEntry.COLUMN_FROM_DATE +" string UNIQUE, "+
                AttendanceEntry.COLUMN_TO_DATE+" string UNIQUE, "+
                AttendanceEntry.COLUMN_CLASSESPRESENT+" int NOT NULL, " +
                AttendanceEntry.COLUMN_TOTAL_CLASSES+" int NOT NULL, "+
                AttendanceEntry.COLUMN_EXTRAINFO+" string);";

       // String SQL_CREATE_ATTENDEDDATES_TABLE = "CREATE TABLE "+AttendanceEntry.TABLE_ATTENDED_DATES+"("+
       //         AttendanceEntry.COLUMN_DATE+" string PRIMARY KEY);";

        sqLiteDatabase.execSQL(SQL_CREATE_DAILYATTENDANCE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BATCHATTENDANCE_TABLE);
        //sqLiteDatabase.execSQL(SQL_CREATE_ATTENDEDDATES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
