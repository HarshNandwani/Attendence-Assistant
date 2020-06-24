package com.hntechs.attendanceassistant.data;

import android.provider.BaseColumns;

public final class AttendanceAssistantContract {

    public static class AttendanceEntry implements BaseColumns{
        //The DailyAttendance Table
        public final static String TABLE_DAILY_ATTENDANCE ="DailyAttendance";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DATE ="Date";
        public final static String COLUMN_CLASSESPRESENT = "NoOfClassesPresent";
        public final static String COLUMN_TOTAL_CLASSES = "TotalNoOfClasses";
        public final static String COLUMN_EXTRAINFO ="ExtraInfo";


        public final static String TABLE_BATCH_ATTENDANCE = "BatchAttendance";
        public final static String COLUMN_FROM_DATE = "FromDate";
        public final static String COLUMN_TO_DATE = "ToDate";

        //public final static String TABLE_ATTENDED_DATES = "AttendedDates";
    }

}
